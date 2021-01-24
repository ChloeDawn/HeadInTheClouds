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

package dev.sapphic.clouds.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.sapphic.clouds.HeadInTheClouds;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldRenderer.class)
@SuppressWarnings("deprecation") // Forge patch
abstract class CloudTransformer implements IResourceManagerReloadListener/*, AutoCloseable*/ {
  @ModifyVariable(method = "renderClouds(Lcom/mojang/blaze3d/matrix/MatrixStack;FDDD)V", at = @At("STORE"), index = 19, require = 1, allow = 1)
  private double translate(final double cloudsY, final MatrixStack pose, final float delta, final double viewX, final double viewY) {
    return HeadInTheClouds.getRelativeY(cloudsY, viewY);
  }
}
