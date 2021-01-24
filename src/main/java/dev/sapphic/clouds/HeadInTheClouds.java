/*
 * Copyright 2021 Chloe Dawn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.sapphic.clouds;

import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod("headintheclouds")
public final class HeadInTheClouds {
  private static final double CLOUD_HEIGHT_FRACTION = 0.25;
  private static final double CAMERA_PARALLAX_FRACTION = 128 * 0.005;

  public HeadInTheClouds() {
    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> {
      return Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (s, v) -> true);
    });
  }

  public static double getRelativeY(final double cloudsY, final double viewY) {
    return (cloudsY - (cloudsY * CLOUD_HEIGHT_FRACTION)) + (viewY * CAMERA_PARALLAX_FRACTION);
  }

  @Override
  public String toString() {
    return "HeadInTheClouds";
  }
}
