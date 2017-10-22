/**
 * Copyright (c) 2016 - 2017 Syncleus, Inc.
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


public final class Body{
   public static Body[] allBodies;

   final private boolean isHeavy;

   public Body(float x, float y, float z, float _m, boolean isHeavy) {
      this.x = x;
      this.y = y;
      this.z = z;
      m = _m;
      this.isHeavy = isHeavy;
   }

   float x, y, z, m, vx, vy, vz;

   public boolean isHeavy() {
      return isHeavy;
   }

   public float getX() {
      return x;
   }

   public float getY() {
      return y;
   }

   public float getZ() {
      return z;
   }

   public float getRadius() {
      return (float) Math.sqrt(x * x + y * y + z* z);
   }

   public float getTheta() {
      return (float) Math.acos(z / getRadius());
   }

   public float getPhi() {
      return (float) Math.atan(y / x);
   }

   public float getVx() {
      return vx;
   }

   public float getVy() {
      return vy;
   }

   public float getVz() {
      return vz;
   }

   public float getM() {
      return m;
   }

   public void setM(float _m) {
      m = _m;
   }

   public void setX(float _x) {
      x = _x;
   }

   public void setY(float _y) {
      y = _y;
   }

   public void setZ(float _z) {
      z = _z;
   }

   public void setRadius(float radius) {
      final float theta = getTheta();
      final float phi = getPhi();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   public void setTheta(float theta) {
      final float radius = getRadius();
      final float phi = getPhi();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   public void setPhi(float phi) {
      final float radius = getRadius();
      final float theta = getTheta();
      x = (float) (radius * Math.cos(theta) * Math.sin(phi));
      y = (float) (radius * Math.sin(theta) * Math.sin(phi));
      z = (float) (radius * Math.cos(phi));
   }

   public void setVx(float _vx) {
      vx = _vx;
   }

   public void setVy(float _vy) {
      vy = _vy;
   }

   public void setVz(float _vz) {
      vz = _vz;
   }
}
