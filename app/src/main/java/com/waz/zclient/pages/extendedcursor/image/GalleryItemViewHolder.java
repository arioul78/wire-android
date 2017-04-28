/**
 * Wire
 * Copyright (C) 2016 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.pages.extendedcursor.image;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.waz.api.ImageAsset;
import com.waz.api.ImageAssetFactory;
import com.waz.utils.wrappers.AndroidURIUtil;
import com.waz.zclient.views.images.ImageAssetView;

import java.io.File;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageAssetView imageView;
    private ImageAsset asset;
    private CursorImagesLayout.Callback callback;

    public GalleryItemViewHolder(ImageAssetView itemView) {
        super(itemView);

        this.imageView = itemView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asset != null && callback != null) {
                    callback.onGalleryPictureSelected(asset);
                }
            }
        });
    }

    public void setCallback(CursorImagesLayout.Callback callback) {
        this.callback = callback;
    }

    public void setPath(String path) {
        asset = ImageAssetFactory.getImageAsset(AndroidURIUtil.fromFile(new File(path)));
        imageView.setImageAsset(asset);
    }
}
