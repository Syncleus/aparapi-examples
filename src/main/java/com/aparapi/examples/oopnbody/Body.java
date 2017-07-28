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

import java.util.List;

public final class Body{
   protected final float delT = .005f;

   protected final float espSqr = 1.0f;

   public static Body[] allBodies;

   public Body(float _x, float _y, float _z, float _m) {
      x = _x;
      y = _y;
      z = _z;
      m = _m;
   }

   float x, y, z, m, vx, vy, vz;

   public float getX() {
      return x;
   }

   public float getY() {
      return y;
   }

   public float getZ() {
      return z;
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
