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
package com.waz.zclient.pages.main.conversation.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waz.api.Asset;
import com.waz.api.AssetStatus;
import com.waz.api.Message;
import com.waz.zclient.R;
import com.waz.zclient.core.api.scala.ModelObserver;
import com.waz.zclient.utils.ViewUtils;

import java.util.Set;

public class MessageBottomSheetDialog extends BottomSheetDialog {

    private final Message message;
    private final Callback callback;
    private Set<MessageAction> chosenOperations = null;

    public enum MessageAction {
        FORWARD(R.id.message_bottom_menu_item_forward, R.string.glyph__share, R.string.message_bottom_menu_action_forward),
        COPY(R.id.message_bottom_menu_item_copy, R.string.glyph__copy, R.string.message_bottom_menu_action_copy),
        DELETE(R.id.message_bottom_menu_item_delete, R.string.glyph__trash, R.string.message_bottom_menu_action_delete),
        DELETE_LOCAL(R.id.message_bottom_menu_item_delete_local, R.string.glyph__delete_me, R.string.message_bottom_menu_action_delete_local),
        DELETE_GLOBAL(R.id.message_bottom_menu_item_delete_global, R.string.glyph__delete_everywhere, R.string.message_bottom_menu_action_delete_global),
        LIKE(R.id.message_bottom_menu_item_like, R.string.glyph__like, R.string.message_bottom_menu_action_like),
        UNLIKE(R.id.message_bottom_menu_item_unlike, R.string.glyph__liked, R.string.message_bottom_menu_action_unlike),
        SAVE(R.id.message_bottom_menu_item_save, R.string.glyph__download, R.string.message_bottom_menu_action_save),
        OPEN_FILE(R.id.message_bottom_menu_item_open_file, R.string.glyph__file, R.string.message_bottom_menu_action_open),
        REVEAL(R.id.message_bottom_menu_item_reveal, R.string.glyph__view, R.string.message_bottom_menu_action_reveal),
        EDIT(R.id.message_bottom_menu_item_edit, R.string.glyph__edit, R.string.message_bottom_menu_action_edit);

        public int resId;
        public int glyphResId;
        public int stringId;

        MessageAction(int resId, int glyphResId, int stringId) {
            this.resId = resId;
            this.glyphResId = glyphResId;
            this.stringId = stringId;
        }
    }

    public MessageBottomSheetDialog(@NonNull Context context, int theme, @NonNull Message message, boolean isMemberOfConversation, boolean isCollection, Callback callback) {
        super(context, theme);
        this.message = message;
        this.callback = callback;
        init(isMemberOfConversation, isCollection, true);
    }

    public MessageBottomSheetDialog(@NonNull Context context, int theme, @NonNull Message message, boolean isMemberOfConversation, boolean isCollection, Callback callback, Set<MessageAction> operations) {
        super(context, theme);
        this.message = message;
        this.callback = callback;
        chosenOperations = operations;
        init(isMemberOfConversation, isCollection, false);
    }

    private void updateOptions(LinearLayout view, boolean isMemberOfConversation, boolean isCollection, boolean deleteCollapsed) {
        view.removeAllViews();
        if (isCopyAllowed()) {
            addAction(view, MessageAction.COPY);
        }
        if (isOpenFileAllowed(isCollection)) {
            addAction(view, MessageAction.OPEN_FILE);
        }
        if (isEditAllowed(isMemberOfConversation, isCollection)) {
            addAction(view, MessageAction.EDIT);
        }
        if (isMemberOfConversation && isLikeAllowed()) {
            if (message.isLikedByThisUser()) {
                addAction(view, MessageAction.UNLIKE);
            } else {
                addAction(view, MessageAction.LIKE);
            }
        }
        if (isSaveAllowed()) {
            addAction(view, MessageAction.SAVE);
        }
        if (isForwardAllowed()) {
            addAction(view, MessageAction.FORWARD);
        }
        if (deleteCollapsed && (isDeleteLocalAllowed() || isDeleteForEveryoneAllowed(isMemberOfConversation))) {
            addAction(view, MessageAction.DELETE);
        } else {
            if (isDeleteLocalAllowed()) {
                addAction(view, MessageAction.DELETE_LOCAL);
            }
            if (isDeleteForEveryoneAllowed(isMemberOfConversation)) {
                addAction(view, MessageAction.DELETE_GLOBAL);
            }
        }

        if (isRevealAllowed(isCollection)) {
            addAction(view, MessageAction.REVEAL);
        }
    }

    @SuppressLint("InflateParams")
    private void init(final boolean isMemberOfConversation, final boolean isCollection, final boolean deleteCollapsed) {
        final LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.message__bottom__menu, null);

        ModelObserver<Asset> assetModelObserver = new ModelObserver<Asset>() {
            @Override
            public void updated(Asset model) {
                if (!model.isEmpty()) {
                    updateOptions(view, isMemberOfConversation, isCollection, deleteCollapsed);
                }
            }
        };

        assetModelObserver.setAndUpdate(message.getAsset());
        updateOptions(view, isMemberOfConversation, isCollection, deleteCollapsed);
        setContentView(view);
    }

    private void addAction(ViewGroup root, final MessageAction action) {
        LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.message__bottom__menu__row, root, false);
        row.setId(action.resId);

        TextView icon = (TextView) row.getChildAt(0);
        icon.setText(action.glyphResId);

        TextView label = (TextView) row.getChildAt(1);
        label.setText(action.stringId);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAction(action, message);
                dismiss();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtils.toPx(getContext(), 48));
        root.addView(row, params);
    }

    private boolean isLikeAllowed() {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.LIKE)) {
            return false;
        }
        if (message.isEphemeral()) {
            return false;
        }
        switch (message.getMessageType()) {
            case ANY_ASSET:
            case ASSET:
            case AUDIO_ASSET:
            case LOCATION:
            case TEXT:
            case TEXT_EMOJI_ONLY:
            case RICH_MEDIA:
            case VIDEO_ASSET:
                return true;
            default:
                return false;
        }
    }

    private boolean isSaveAllowed() {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.SAVE)) {
            return false;
        }
        if (message.isEphemeral()) {
            return false;
        }
        switch (message.getMessageType()) {
            // Only image supported ATM, need to handle Audio/File/Video
            case ASSET:
                return true;
            case AUDIO_ASSET:
            case VIDEO_ASSET:
                return message.getAsset().getStatus() == AssetStatus.UPLOAD_DONE ||
                    message.getAsset().getStatus() == AssetStatus.DOWNLOAD_DONE;
            default:
                return false;
        }
    }

    private boolean isOpenFileAllowed(boolean isCollection) {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.OPEN_FILE)) {
            return false;
        }
        if (message.isEphemeral()) {
            return false;
        }
        if (isCollection) {
            return false;
        }
        switch (message.getMessageType()) {
            case ANY_ASSET:
                return message.getAsset().getStatus() == AssetStatus.UPLOAD_DONE ||
                    message.getAsset().getStatus() == AssetStatus.DOWNLOAD_DONE;
            default:
                return false;
        }
    }

    private boolean isCopyAllowed() {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.COPY)) {
            return false;
        }
        if (message.isEphemeral()) {
            return false;
        }
        switch (message.getMessageType()) {
            case TEXT:
            case TEXT_EMOJI_ONLY:
            case RICH_MEDIA:
                return true;
            default:
                return false;
        }
    }

    private boolean isForwardAllowed() {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.FORWARD)) {
            return false;
        }
        if (message.isEphemeral()) {
            return false;
        }
        switch (message.getMessageType()) {
            case TEXT:
            case TEXT_EMOJI_ONLY:
            case RICH_MEDIA:
                return true;
            case ANY_ASSET:
            case AUDIO_ASSET:
            case VIDEO_ASSET:
                return message.getAsset().getStatus() == AssetStatus.UPLOAD_DONE ||
                    message.getAsset().getStatus() == AssetStatus.DOWNLOAD_DONE;
            case ASSET:
                // TODO: Once https://wearezeta.atlassian.net/browse/CM-976 is resolved, we should handle image asset like any other asset
                return true;
            default:
                return false;
        }
    }

    private boolean isEditAllowed(boolean isMemberOfConversation, boolean isCollection) {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.EDIT)) {
            return false;
        }
        if (!isMemberOfConversation ||
            message.isEphemeral() ||
            !message.getUser().isMe() ||
            isCollection) {
            return false;
        }
        switch (message.getMessageType()) {
            case TEXT_EMOJI_ONLY:
            case TEXT:
            case RICH_MEDIA:
                return true;
            default:
                return false;
        }
    }

    private boolean isDeleteForEveryoneAllowed(boolean isMemberOfConversation) {
        if (chosenOperations != null && !chosenOperations.contains(MessageAction.DELETE_GLOBAL)) {
            return false;
        }
        if (!isMemberOfConversation ||
            !message.getUser().isMe()) {
            return false;
        }
        switch (message.getMessageType()) {
            case TEXT:
            case ANY_ASSET:
            case ASSET:
            case AUDIO_ASSET:
            case VIDEO_ASSET:
            case KNOCK:
            case LOCATION:
            case RICH_MEDIA:
            case TEXT_EMOJI_ONLY:
                return true;
            default:
                return false;
        }
    }

    private boolean isDeleteLocalAllowed() {
        return !(chosenOperations != null && !chosenOperations.contains(MessageAction.DELETE_LOCAL));
    }

    private boolean isRevealAllowed(Boolean isCollection) {
        return !(!isCollection || (chosenOperations != null && !chosenOperations.contains(MessageAction.REVEAL)));
    }

    public interface Callback {
        void onAction(MessageAction action, Message message);
    }

}
