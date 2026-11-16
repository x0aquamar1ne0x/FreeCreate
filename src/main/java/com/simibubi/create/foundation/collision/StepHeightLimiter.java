package com.simibubi.create.foundation.collision;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Works out how far up an entity may actually step when colliding with a contraption.
 * <p>
 * Create resolves contraption collisions box by box: any single box that the entity overlaps by no more than
 * its step height is stepped onto, without checking what is above it. With the vanilla step height (0.6) a full
 * block can never be stepped onto, so this is harmless. With a step height of 1 or more (Apotheosis, Mekanism Free
 * Runners, ...) the bottom block of every wall becomes steppable, and the entity climbs the wall one block per tick
 * until it ends up on the roof.
 * <p>
 * This mirrors the vanilla rule instead: a step is only allowed if the entity fits at the stepped up position and
 * nothing blocks the way up. If no such step exists, the step height is capped at the vanilla default.
 */
public final class StepHeightLimiter {

	/** Default step height of players and most mobs. Create's contraption collision was tuned with this value. */
	public static final float VANILLA_STEP = 0.6f;

	private static final double EPSILON = 1.0E-3;

	private StepHeightLimiter() {}

	/**
	 * @param colliders        the contraption's collision boxes, in contraption-local space
	 * @param entityBox        the entity's bounding box, in contraption-local space
	 * @param maxStep          the entity's step height
	 * @param verticalRotation whether the contraption is pitched or rolled, so local up is not world up
	 * @return the step height Create should use for this collision pass
	 */
	public static float limit(CollisionList colliders, OrientedBB entityBox, float maxStep, boolean verticalRotation) {
		if (maxStep <= VANILLA_STEP)
			return maxStep;

		// Stepping happens along the contraption's local up axis, which only matches the world up axis
		// for contraptions that just rotate around Y. Keep it simple for everything else.
		if (verticalRotation)
			return VANILLA_STEP;

		AABB box = localBounds(entityBox);
		double foot = box.minY;
		double height = box.getYsize();
		double middle = foot + height / 2;

		double bestStep = 0;
		for (int i = 0; i < colliders.size; i++) {
			if (!overlapsHorizontally(colliders, i, box))
				continue;

			double bottom = colliders.centerY[i] - colliders.extentsY[i];
			double top = colliders.centerY[i] + colliders.extentsY[i];

			// Only obstacles that start below the entity's middle and end within reach can be stepped onto
			if (bottom >= middle || top <= foot || top > box.maxY)
				continue;

			double step = top - foot;
			if (step <= bestStep || step > maxStep)
				continue;

			if (canStandAt(colliders, box, middle, top + EPSILON, height))
				bestStep = step;
		}

		// Leave a little slack, Create compares against slightly different separation values
		return (float) Math.min(maxStep, Math.max(VANILLA_STEP, bestStep + EPSILON * 10));
	}

	/**
	 * Checks the whole column the entity would pass through on its way up: every box in it must be an obstacle
	 * the entity steps over, i.e. low enough to end up below its feet.
	 */
	private static boolean canStandAt(CollisionList colliders, AABB box, double middle, double newFoot, double height) {
		double columnBottom = box.minY + EPSILON;
		double columnTop = newFoot + height;

		for (int i = 0; i < colliders.size; i++) {
			if (!overlapsHorizontally(colliders, i, box))
				continue;

			double bottom = colliders.centerY[i] - colliders.extentsY[i];
			double top = colliders.centerY[i] + colliders.extentsY[i];
			if (top <= columnBottom || bottom >= columnTop)
				continue;

			boolean steppedOver = top <= newFoot && bottom < middle;
			if (!steppedOver)
				return false;
		}

		return true;
	}

	private static boolean overlapsHorizontally(CollisionList colliders, int i, AABB box) {
		return Math.abs(colliders.centerX[i] - (box.minX + box.maxX) / 2) < colliders.extentsX[i] + box.getXsize() / 2 - EPSILON
			&& Math.abs(colliders.centerZ[i] - (box.minZ + box.maxZ) / 2) < colliders.extentsZ[i] + box.getZsize() / 2 - EPSILON;
	}

	/**
	 * The axis aligned bounds of the entity's oriented box in contraption-local space.
	 */
	private static AABB localBounds(OrientedBB obb) {
		AABB unrotated = obb.getAsAABB();
		double ex = unrotated.getXsize() / 2;
		double ey = unrotated.getYsize() / 2;
		double ez = unrotated.getZsize() / 2;

		// Columns of the rotation matrix are the entity's axes expressed in contraption-local space
		Matrix3d rotation = obb.getRotation();
		Vec3 axisX = rotation.transform(1, 0, 0);
		Vec3 axisY = rotation.transform(0, 1, 0);
		Vec3 axisZ = rotation.transform(0, 0, 1);

		double hx = Math.abs(axisX.x) * ex + Math.abs(axisY.x) * ey + Math.abs(axisZ.x) * ez;
		double hy = Math.abs(axisX.y) * ex + Math.abs(axisY.y) * ey + Math.abs(axisZ.y) * ez;
		double hz = Math.abs(axisX.z) * ex + Math.abs(axisY.z) * ey + Math.abs(axisZ.z) * ez;

		Vec3 center = obb.getCenter();
		return new AABB(center.x - hx, center.y - hy, center.z - hz, center.x + hx, center.y + hy, center.z + hz);
	}
}
