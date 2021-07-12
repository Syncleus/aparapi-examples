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
package com.aparapi.examples.oopnbody;

class Perspective {
   private float xeye;

   private float yeye;

   private float zeye;

   private float xat;

   private float yat;

   private float zat;

   public float zoomFactor;

   /**
    * <p>Constructor for Perspective.</p>
    */
   public Perspective() {
   }

   /**
    * <p>Constructor for Perspective.</p>
    *
    * @param xeye a float.
    * @param yeye a float.
    * @param zeye a float.
    * @param xat a float.
    * @param yat a float.
    * @param zat a float.
    * @param zoomFactor a float.
    */
   public Perspective(float xeye, float yeye, float zeye, float xat, float yat, float zat, float zoomFactor) {
      this.xeye = xeye;
      this.yeye = yeye;
      this.zeye = zeye;
      this.xat = xat;
      this.yat = yat;
      this.zat = zat;
      this.zoomFactor = zoomFactor;
   }

   /**
    * <p>Getter for the field <code>xeye</code>.</p>
    *
    * @return a float.
    */
   public float getXeye() {
      return xeye;
   }

   /**
    * <p>Setter for the field <code>xeye</code>.</p>
    *
    * @param xeye a float.
    */
   public void setXeye(float xeye) {
      this.xeye = xeye;
   }

   /**
    * <p>Getter for the field <code>yeye</code>.</p>
    *
    * @return a float.
    */
   public float getYeye() {
      return yeye;
   }

   /**
    * <p>Setter for the field <code>yeye</code>.</p>
    *
    * @param yeye a float.
    */
   public void setYeye(float yeye) {
      this.yeye = yeye;
   }

   /**
    * <p>Getter for the field <code>zeye</code>.</p>
    *
    * @return a float.
    */
   public float getZeye() {
      return zeye;
   }

   /**
    * <p>Setter for the field <code>zeye</code>.</p>
    *
    * @param zeye a float.
    */
   public void setZeye(float zeye) {
      this.zeye = zeye;
   }

   /**
    * <p>Getter for the field <code>xat</code>.</p>
    *
    * @return a float.
    */
   public float getXat() {
      return xat;
   }

   /**
    * <p>Setter for the field <code>xat</code>.</p>
    *
    * @param xat a float.
    */
   public void setXat(float xat) {
      this.xat = xat;
   }

   /**
    * <p>Getter for the field <code>yat</code>.</p>
    *
    * @return a float.
    */
   public float getYat() {
      return yat;
   }

   /**
    * <p>Setter for the field <code>yat</code>.</p>
    *
    * @param yat a float.
    */
   public void setYat(float yat) {
      this.yat = yat;
   }

   /**
    * <p>Getter for the field <code>zat</code>.</p>
    *
    * @return a float.
    */
   public float getZat() {
      return zat;
   }

   /**
    * <p>Setter for the field <code>zat</code>.</p>
    *
    * @param zat a float.
    */
   public void setZat(float zat) {
      this.zat = zat;
   }

   /**
    * <p>Getter for the field <code>zoomFactor</code>.</p>
    *
    * @return a float.
    */
   public float getZoomFactor() {
      return zoomFactor;
   }

   /**
    * <p>Setter for the field <code>zoomFactor</code>.</p>
    *
    * @param zoomFactor a float.
    */
   public void setZoomFactor(float zoomFactor) {
      this.zoomFactor = zoomFactor;
   }

   /**
    * <p>getRadius.</p>
    *
    * @return a float.
    */
   public float getRadius() {
      return (float) Math.sqrt(xeye * xeye + yeye * yeye + zeye * zeye);
   }

   /**
    * <p>getTheta.</p>
    *
    * @return a float.
    */
   public float getTheta() {
      if( getRadius()  == 0f)
         return 0f;
      return (float) Math.acos(zeye / getRadius());
   }

   /**
    * <p>getPhi.</p>
    *
    * @return a float.
    */
   public float getPhi() {
      if(xeye == 0f)
         return 0f;
      return (float) Math.atan(yeye / xeye);
   }

   /**
    * <p>setRadius.</p>
    *
    * @param radius a float.
    */
   public void setRadius(float radius) {
      final float theta = getTheta();
      final float phi = getPhi();
      xeye = radius * ((float)Math.cos(phi)) * ((float)Math.sin(theta));
      yeye = radius * ((float)Math.sin(theta)) * ((float)Math.sin(phi));
      zeye = radius * ((float)Math.cos(theta));
   }

   /**
    * <p>setTheta.</p>
    *
    * @param theta a float.
    */
   public void setTheta(float theta) {
      final float radius = getRadius();
      final float phi = getPhi();
      xeye = radius * ((float)Math.cos(phi)) * ((float)Math.sin(theta));
      yeye = radius * ((float)Math.sin(theta)) * ((float)Math.sin(phi));
      zeye = radius * ((float)Math.cos(theta));
   }

   /**
    * <p>setPhi.</p>
    *
    * @param phi a float.
    */
   public void setPhi(float phi) {
      final float radius = getRadius();
      final float theta = getTheta();
      xeye = radius * ((float)Math.cos(phi)) * ((float)Math.sin(theta));
      yeye = radius * ((float)Math.sin(theta)) * ((float)Math.sin(phi));
      zeye = radius * ((float)Math.cos(theta));
   }

   /** {@inheritDoc} */
   @Override
   public String toString() {
      return "Perspective{" +
              "  xeye=" + xeye +
              ", yeye=" + yeye +
              ", zeye=" + zeye +
              "  radius=" + getRadius() +
              ", theta=" + getTheta() +
              ", phi=" + getPhi() +
              ", xat=" + xat +
              ", yat=" + yat +
              ", zat=" + zat +
              ", zoomFactor=" + zoomFactor +
              '}';
   }
}
