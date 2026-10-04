package ru.roughcipher.spike.util;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.List;

public final class EntityBlockSeparation {

	private static final double EPSILON = 1.0E-4;

	private EntityBlockSeparation() {
	}

	public static boolean pushOut(Entity entity) {
		World world = entity.world;
		if (entity.removed || entity.hasNoPhysics()) {
			return true;
		}

		AABBd bb = entity.bb;
		List<AABBdc> colliding = world.getCubes(entity, bb);
		if (colliding.isEmpty()) {
			return true;
		}

		boolean freeNegX = true;
		boolean freePosX = true;
		boolean freeNegZ = true;
		boolean freePosZ = true;

		int bx = MathHelper.floor(entity.x);
		int bz = MathHelper.floor(entity.z);
		int minY = MathHelper.floor(bb.minY);
		int maxY = MathHelper.floor(bb.maxY);
		TilePos pos = new TilePos();

		for (int y = minY; y <= maxY; y++) {
			if (world.isBlockNormalCube(pos.set(bx - 1, y, bz))) {
				freeNegX = false;
			}
			if (world.isBlockNormalCube(pos.set(bx + 1, y, bz))) {
				freePosX = false;
			}
			if (world.isBlockNormalCube(pos.set(bx, y, bz - 1))) {
				freeNegZ = false;
			}
			if (world.isBlockNormalCube(pos.set(bx, y, bz + 1))) {
				freePosZ = false;
			}
		}

		boolean moved = false;
		for (AABBdc block : colliding) {
			if (!intersects(bb, block)) {
				continue;
			}
			if (resolveAgainst(entity, world, bb, block, freeNegX, freePosX, freeNegZ, freePosZ)) {
				moved = true;
			}
		}

		if (moved) {
			syncPosFromBb(entity, bb);
		}

		return world.getCubes(entity, entity.bb).isEmpty();
	}

	private static boolean resolveAgainst(
		Entity entity,
		World world,
		AABBd bb,
		AABBdc block,
		boolean freeNegX,
		boolean freePosX,
		boolean freeNegZ,
		boolean freePosZ
	) {
		double penNegX = bb.maxX - block.minX();
		double penPosX = block.maxX() - bb.minX;
		double penNegZ = bb.maxZ - block.minZ();
		double penPosZ = block.maxZ() - bb.minZ;

		double bestDx = 0.0;
		double bestDz = 0.0;
		double bestAbs = Double.POSITIVE_INFINITY;
		boolean found = false;

		if (freeNegX && penNegX > 0.0 && penNegX < bestAbs) {
			bestAbs = penNegX;
			bestDx = -(penNegX + EPSILON);
			bestDz = 0.0;
			found = true;
		}
		if (freePosX && penPosX > 0.0 && penPosX < bestAbs) {
			bestAbs = penPosX;
			bestDx = penPosX + EPSILON;
			bestDz = 0.0;
			found = true;
		}
		if (freeNegZ && penNegZ > 0.0 && penNegZ < bestAbs) {
			bestAbs = penNegZ;
			bestDx = 0.0;
			bestDz = -(penNegZ + EPSILON);
			found = true;
		}
		if (freePosZ && penPosZ > 0.0 && penPosZ < bestAbs) {
			bestDx = 0.0;
			bestDz = penPosZ + EPSILON;
			found = true;
		}

		if (!found) {
			return false;
		}

		bestDx = clampX(entity, world, bb, bestDx);
		bestDz = clampZ(entity, world, bb, bestDz);

		if (bestDx == 0.0 && bestDz == 0.0) {
			return false;
		}

		bb.translate(bestDx, 0.0, bestDz);
		return true;
	}

	private static double clampX(Entity entity, World world, AABBd bb, double dx) {
		if (dx == 0.0) {
			return 0.0;
		}
		AABBd expanded = new AABBd(bb).translate(dx, 0.0, 0.0);
		AABBd sweep = new AABBd(bb).union(expanded);
		List<AABBdc> nearby = world.getCubes(entity, sweep);
		for (AABBdc other : nearby) {
			dx = calculateXOffset(bb, other, dx);
			if (dx == 0.0) {
				return 0.0;
			}
		}
		return dx;
	}

	private static double clampZ(Entity entity, World world, AABBd bb, double dz) {
		if (dz == 0.0) {
			return 0.0;
		}
		AABBd expanded = new AABBd(bb).translate(0.0, 0.0, dz);
		AABBd sweep = new AABBd(bb).union(expanded);
		List<AABBdc> nearby = world.getCubes(entity, sweep);
		for (AABBdc other : nearby) {
			dz = calculateZOffset(bb, other, dz);
			if (dz == 0.0) {
				return 0.0;
			}
		}
		return dz;
	}

	static double calculateXOffset(AABBdc self, AABBdc other, double dx) {
		if (self.maxY() <= other.minY() || self.minY() >= other.maxY()) {
			return dx;
		}
		if (self.maxZ() <= other.minZ() || self.minZ() >= other.maxZ()) {
			return dx;
		}
		if (dx > 0.0 && self.maxX() <= other.minX()) {
			double gap = other.minX() - self.maxX();
			if (gap < dx) {
				dx = gap;
			}
		} else if (dx < 0.0 && self.minX() >= other.maxX()) {
			double gap = other.maxX() - self.minX();
			if (gap > dx) {
				dx = gap;
			}
		}
		return dx;
	}

	static double calculateZOffset(AABBdc self, AABBdc other, double dz) {
		if (self.maxX() <= other.minX() || self.minX() >= other.maxX()) {
			return dz;
		}
		if (self.maxY() <= other.minY() || self.minY() >= other.maxY()) {
			return dz;
		}
		if (dz > 0.0 && self.maxZ() <= other.minZ()) {
			double gap = other.minZ() - self.maxZ();
			if (gap < dz) {
				dz = gap;
			}
		} else if (dz < 0.0 && self.minZ() >= other.maxZ()) {
			double gap = other.maxZ() - self.minZ();
			if (gap > dz) {
				dz = gap;
			}
		}
		return dz;
	}

	static boolean intersects(AABBdc a, AABBdc b) {
		return a.minX() < b.maxX() && a.maxX() > b.minX()
			&& a.minY() < b.maxY() && a.maxY() > b.minY()
			&& a.minZ() < b.maxZ() && a.maxZ() > b.minZ();
	}

	private static void syncPosFromBb(Entity entity, AABBd bb) {
		double newX = (bb.minX + bb.maxX) * 0.5;
		double newY = bb.minY + entity.heightOffset - entity.ySlideOffset;
		double newZ = (bb.minZ + bb.maxZ) * 0.5;
		entity.setPos(newX, newY, newZ);
	}
}
