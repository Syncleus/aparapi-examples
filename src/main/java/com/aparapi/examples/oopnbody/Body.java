/**
 * Copyright (c) 2016 - 2018 Syncleus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * This product currently only contains code developed by authors
 * of specific components, as identified by the source code files.
 *
 * Since product implements StAX API, it has dependencies to StAX API
 * classes.
 *
 * For additional credits (generally to people who reported problems)
 * see CREDITS file.
 */
package com.aparapi.examples.oopnbody;


/**
 * <p>Body class.</p>
 *
 * @author freemo
 * @version $Id: $Id
 */
public final class Body{
   /** Constant <code>allBodies</code> */
   public static Body[] allBodies;

   final private boolean isHeavy;

   /**
    * <p>Constructor for Body.</p>
    *
    * @param x a float.
    * @param y a float.
    * @param z a float.
    * @param _m a float.
    * @param isHeavy a boolean.
    */
   public Body(float x, float y, float z, float _m, boolean isHeavy) {
      this.x = x;
      this.y = y;
      this.z = z;
      m = _m;
      this.isHeavy = isHeavy;
   }

   float x, y, z, m, vx, vy, vz;

   /**
    * <p>isHeavy.</p>
    *
    * @return a boolean.
    */
   public boolean isHeavy() {
      return isHeavy;
   }

   /**
    * <p>Getter for the field <code>x</code>.</p>
    *
    * @return a float.
    */
   public float getX() {
      return x;
   }

   /**
    * <p>Getter for the field <code>y</code>.</p>
    *
    * @return a float.
    */
   public float getY() {
      return y;
   }

   /**
    * <p>Getter for the field <code>z</code>.</p>
    *
    * @return a float.
    */
   public float getZ() {
      return z;
   }

   /**
    * <p>getRadius.</p>
    *
    * @return a float.
    */
   public float getRadius() {
      return (float) Math.sqrt(x * x + y * y + z* z);
   }

   /**
    * <p>getTheta.</p>
    *
    * @return a float.
    */
   public float getTheta() {
      return (float) Math.acos(z / getRadius());
   }

   /**
    * <p>getPhi.</p>
    *
    * @return a float.
    */
   public float getPhi() {
      return (float) Math.atan(y / x);
   }

   /**
    * <p>Getter for the field <code>vx</code>.</p>
    *
    * @return a float.
    */
   public float getVx() {
      return vx;
   }

   /**
    * <p>Getter for the field <code>vy</code>.</p>
    *
    * @return a float.
    */
   public float getVy() {
      return vy;
   }

   /**
    * <p>Getter for the field <code>vz</code>.</p>
    *
    * @return a float.
    */
   public float getVz() {
      return vz;
   }

   /**
    * <p>Getter for the field <code>m</code>.</p>
    *
    * @return a float.
    */
   public float getM() {
      return m;
   }

   /**
    * <p>Setter for the field <code>m</code>.</p>
    *
    * @param _m a float.
    */
   public void setM(float _m) {
      m = _m;
   }

   /**
    * <p>Setter for the field <code>x</code>.</p>
    *
    * @param _x a float.
    */
   public void setX(float _x) {
      x = _x;
   }

   /**
    * <p>Setter for the field <code>y</code>.</p>
    *
    * @param _y a float.
    */
   public void setY(float _y) {
      y = _y;
   }

   /**
    * <p>Setter for the field <code>z</code>.</p>
    *
    * @param _z a float.
    */
   public void setZ(float _z) {
      z = _z;
   }

   /**
    * <p>setRadius.</p>
    *
    * @param radius a float.
    */
   public void setRadius(float radius) {
      final float theta = getTheta();
      final float phi = getPhi();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   /**
    * <p>setTheta.</p>
    *
    * @param theta a float.
    */
   public void setTheta(float theta) {
      final float radius = getRadius();
      final float phi = getPhi();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   /**
    * <p>setPhi.</p>
    *
    * @param phi a float.
    */
   public void setPhi(float phi) {
      final float radius = getRadius();
      final float theta = getTheta();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   /**
    * <p>Setter for the field <code>vx</code>.</p>
    *
    * @param _vx a float.
    */
   public void setVx(float _vx) {
      vx = _vx;
   }

   /**
    * <p>Setter for the field <code>vy</code>.</p>
    *
    * @param _vy a float.
    */
   public void setVy(float _vy) {
      vy = _vy;
   }

   /**
    * <p>Setter for the field <code>vz</code>.</p>
    *
    * @param _vz a float.
    */
   public void setVz(float _vz) {
      vz = _vz;
   }
}
