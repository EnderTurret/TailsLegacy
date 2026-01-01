// Quaternionf from JOML
/*
 * The MIT License
 *
 * Copyright (c) 2015-2022 Richard Greenlees
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.kihira.tails.common.client.math;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.TailsVec3f;

/**
 * Quaternion of 4 single-precision floats which can represent rotation and uniform scaling.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 */
public class Quaternionf {

    private static final long serialVersionUID = 1L;

    /**
     * The first component of the vector part.
     */
    public float x;
    /**
     * The second component of the vector part.
     */
    public float y;
    /**
     * The third component of the vector part.
     */
    public float z;
    /**
     * The real/scalar part of the quaternion.
     */
    public float w;

    /**
     * Create a new {@link Quaternionf} and initialize it with <code>(x=0, y=0, z=0, w=1)</code>,
     * where <code>(x, y, z)</code> is the vector part of the quaternion and <code>w</code> is the real/scalar part.
     */
    public Quaternionf() {
        this.w = 1.0f;
    }

    /**
     * Create a new {@link Quaternionf} and initialize its components to the given values.
     *
     * @param x
     *          the first component of the imaginary part
     * @param y
     *          the second component of the imaginary part
     * @param z
     *          the third component of the imaginary part
     * @param w
     *          the real part
     */
    public Quaternionf(double x, double y, double z, double w) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
        this.w = (float) w;
    }

    /**
     * Create a new {@link Quaternionf} and initialize its components to the given values.
     *
     * @param x
     *          the first component of the imaginary part
     * @param y
     *          the second component of the imaginary part
     * @param z
     *          the third component of the imaginary part
     * @param w
     *          the real part
     */
    public Quaternionf(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Create a new {@link Quaternionf} and initialize its components to the same values as the given {@link Quaternionf}.
     *
     * @param source
     *          the {@link Quaternionf} to take the component values from
     */
    public Quaternionf(Quaternionf source) {
        set(source);
    }

    /**
     * @return the first component of the vector part
     */
    public float x() {
        return this.x;
    }

    /**
     * @return the second component of the vector part
     */
    public float y() {
        return this.y;
    }

    /**
     * @return the third component of the vector part
     */
    public float z() {
        return this.z;
    }

    /**
     * @return the real/scalar part of the quaternion
     */
    public float w() {
        return this.w;
    }

    /**
     * Normalize this quaternion.
     *
     * @return this
     */
    public Quaternionf normalize() {
        return normalize(this);
    }

    public Quaternionf normalize(Quaternionf dest) {
        float invNorm = (float) (1D / Math.sqrt(fma(x, x, fma(y, y, fma(z, z, w * w)))));
        dest.x = x * invNorm;
        dest.y = y * invNorm;
        dest.z = z * invNorm;
        dest.w = w * invNorm;
        return dest;
    }

    /**
     * Add the quaternion <code>(x, y, z, w)</code> to this quaternion.
     *
     * @param x
     *          the x component of the vector part
     * @param y
     *          the y component of the vector part
     * @param z
     *          the z component of the vector part
     * @param w
     *          the real/scalar component
     * @return this
     */
    public Quaternionf add(float x, float y, float z, float w) {
        return add(x, y, z, w, this);
    }

    public Quaternionf add(float x, float y, float z, float w, Quaternionf dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;
        return dest;
    }

    /**
     * Add <code>q2</code> to this quaternion.
     *
     * @param q2
     *          the quaternion to add to this
     * @return this
     */
    public Quaternionf add(Quaternionf q2) {
        return add(q2, this);
    }

    public Quaternionf add(Quaternionf q2, Quaternionf dest) {
        dest.x = x + q2.x();
        dest.y = y + q2.y();
        dest.z = z + q2.z();
        dest.w = w + q2.w();
        return dest;
    }

    /**
     * Return the dot of this quaternion and <code>otherQuat</code>.
     *
     * @param otherQuat
     *          the other quaternion
     * @return the dot product
     */
    public float dot(Quaternionf otherQuat) {
        return this.x * otherQuat.x + this.y * otherQuat.y + this.z * otherQuat.z + this.w * otherQuat.w;
    }

    /**
     * Set the given {@link Quaternionf} to the values of <code>this</code>.
     *
     * @see #set(Quaternionf)
     *
     * @param dest
     *          the {@link Quaternionf} to set
     * @return the passed in destination
     */
    public Quaternionf get(Quaternionf dest) {
        return dest.set(this);
    }

    /**
     * Set this quaternion to the given values.
     *
     * @param x
     *          the new value of x
     * @param y
     *          the new value of y
     * @param z
     *          the new value of z
     * @param w
     *          the new value of w
     * @return this
     */
    public Quaternionf set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Set this quaternion to be a copy of <code>q</code>.
     *
     * @param q
     *          the {@link Quaternionf} to copy
     * @return this
     */
    public Quaternionf set(Quaternionf q) {
        this.x = q.x();
        this.y = q.y();
        this.z = q.z();
        this.w = q.w();
        return this;
    }

    /**
     * Set this quaternion to be a representation of the supplied axis and
     * angle (in radians).
     *
     * @param axis
     *          the rotation axis
     * @param angle
     *          the angle in radians
     * @return this
     */
    public Quaternionf fromAxisAngleRad(TailsVec3f axis, float angle) {
        return fromAxisAngleRad(axis.x(), axis.y(), axis.z(), angle);
    }

    /**
     * Set this quaternion to be a representation of the supplied axis and
     * angle (in radians).
     *
     * @param axisX
     *          the x component of the rotation axis
     * @param axisY
     *          the y component of the rotation axis
     * @param axisZ
     *          the z component of the rotation axis
     * @param angle
     *          the angle in radians
     * @return this
     */
    public Quaternionf fromAxisAngleRad(float axisX, float axisY, float axisZ, float angle) {
        float hangle = angle / 2.0f;
        float sinAngle = (float) Math.sin(hangle);
        float vLength = (float) Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        x = axisX / vLength * sinAngle;
        y = axisY / vLength * sinAngle;
        z = axisZ / vLength * sinAngle;
        w = (float) cosFromSin(sinAngle, hangle);
        return this;
    }

    private static double cosFromSin(double sin, double angle) {
        // sin(x)^2 + cos(x)^2 = 1
        double cos = Math.sqrt(1.0 - sin * sin);
        double a = angle + TailsMath.HALF_PI;
        double b = a - (int)(a / TailsMath.TWO_PI) * TailsMath.TWO_PI;
        if (b < 0.0)
            b = TailsMath.TWO_PI + b;
        if (b >= TailsMath.PI)
            return -cos;
        return cos;
    }

    /**
     * Set this quaternion to be a representation of the supplied axis and
     * angle (in degrees).
     *
     * @param axis
     *          the rotation axis
     * @param angle
     *          the angle in degrees
     * @return this
     */
    public Quaternionf fromAxisAngleDeg(TailsVec3f axis, float angle) {
        return fromAxisAngleRad(axis.x(), axis.y(), axis.z(), (float) Math.toRadians(angle));
    }

    /**
     * Set this quaternion to be a representation of the supplied axis and
     * angle (in degrees).
     *
     * @param axisX
     *          the x component of the rotation axis
     * @param axisY
     *          the y component of the rotation axis
     * @param axisZ
     *          the z component of the rotation axis
     * @param angle
     *          the angle in radians
     * @return this
     */
    public Quaternionf fromAxisAngleDeg(float axisX, float axisY, float axisZ, float angle) {
        return fromAxisAngleRad(axisX, axisY, axisZ, (float) Math.toRadians(angle));
    }

    /**
     * Multiply this quaternion by <code>q</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given
     * quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = T * Q</code>
     * <p>
     * So, this method uses post-multiplication like the matrix classes, resulting in a
     * vector to be transformed by <code>Q</code> first, and then by <code>T</code>.
     *
     * @param q
     *          the quaternion to multiply <code>this</code> by
     * @return this
     */
    public Quaternionf mul(Quaternionf q) {
        return mul(q, this);
    }

    public Quaternionf mul(Quaternionf q, Quaternionf dest) {
        return dest.set(fma(w, q.x(), fma(x, q.w(), fma(y, q.z(), -z * q.y()))),
                        fma(w, q.y(), fma(-x, q.z(), fma(y, q.w(), z * q.x()))),
                        fma(w, q.z(), fma(x, q.y(), fma(-y, q.x(), z * q.w()))),
                        fma(w, q.w(), fma(-x, q.x(), fma(-y, q.y(), -z * q.z()))));
    }

    /**
     * Multiply this quaternion by the quaternion represented via <code>(qx, qy, qz, qw)</code>.
     * <p>
     * If <code>T</code> is <code>this</code> and <code>Q</code> is the given
     * quaternion, then the resulting quaternion <code>R</code> is:
     * <p>
     * <code>R = T * Q</code>
     * <p>
     * So, this method uses post-multiplication like the matrix classes, resulting in a
     * vector to be transformed by <code>Q</code> first, and then by <code>T</code>.
     *
     * @param qx
     *          the x component of the quaternion to multiply <code>this</code> by
     * @param qy
     *          the y component of the quaternion to multiply <code>this</code> by
     * @param qz
     *          the z component of the quaternion to multiply <code>this</code> by
     * @param qw
     *          the w component of the quaternion to multiply <code>this</code> by
     * @return this
     */
    public Quaternionf mul(float qx, float qy, float qz, float qw) {
        return mul(qx, qy, qz, qw, this);
    }

    public Quaternionf mul(float qx, float qy, float qz, float qw, Quaternionf dest) {
        return dest.set(fma(w, qx, fma(x, qw, fma(y, qz, -z * qy))),
                        fma(w, qy, fma(-x, qz, fma(y, qw, z * qx))),
                        fma(w, qz, fma(x, qy, fma(-y, qx, z * qw))),
                        fma(w, qw, fma(-x, qx, fma(-y, qy, -z * qz))));
    }

    /**
     * Multiply this quaternion by the given scalar.
     * <p>
     * This method multiplies all of the four components by the specified scalar.
     *
     * @param f
     *          the factor to multiply all components by
     * @return this
     */
    public Quaternionf mul(float f) {
        return mul(f, this);
    }

    public Quaternionf mul(float f, Quaternionf dest) {
        dest.x = x * f;
        dest.y = y * f;
        dest.z = z * f;
        dest.w = w * f;
        return dest;
    }

    /**
     * Conjugate this quaternion.
     *
     * @return this
     */
    public Quaternionf conjugate() {
        return conjugate(this);
    }

    public Quaternionf conjugate(Quaternionf dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        dest.w = w;
        return dest;
    }

    /**
     * Set this quaternion to the identity.
     *
     * @return this
     */
    public Quaternionf identity() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
        return this;
    }

    /**
     * Return a string representation of this quaternion.
     * <p>
     *
     * @return the string representation
     */
    @Override
	public String toString() {
        return "(" + x + " " + y + " " + z + " " + w + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(w);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
        w = in.readFloat();
    }

    @Override
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(w);
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(z);
        return result;
    }

    @Override
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Quaternionf other = (Quaternionf) obj;
        if (Float.floatToIntBits(w) != Float.floatToIntBits(other.w))
            return false;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
            return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
            return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
            return false;
        return true;
    }

    private static float fma(float a, float b, float c) {
    	return a * b + c;
    }
}