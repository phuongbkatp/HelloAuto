package com.appian.manutdvietnam.app.comment.presenter;

import com.appian.manutdvietnam.app.BasePresenter;
import com.appian.manutdvietnam.app.comment.view.PostCommentView;
import com.appian.manutdvietnam.data.interactor.CommentsInteractor;
import com.appian.manutdvietnam.data.interactor.OnResponseListener;
import com.appnet.android.football.fbvn.data.DefaultData;

public class PostCommentPresenter extends BasePresenter<PostCommentView> {
    private final CommentsInteractor mCommentsInteractor;
    private OnResponseListener<DefaultData> mOnPostComment;

    public PostCommentPresenter(CommentsInteractor commentsInteractor) {
        mCommentsInteractor = commentsInteractor;
        mOnPostComment = new OnResponseListener<DefaultData>() {
            @Override
            public void onSuccess(DefaultData data) {
                if (getView() == null) {
                    return;
                }
                getView().onPostCommentSuccess();
            }

            @Override
            public void onFailure(String error) {

            }
        };
    }

    public void postComment(String authorization, int userId, String content, String objectType, int objectId) {
        mCommentsInteractor.postComment(authorization, userId, content, objectType, objectId, mOnPostComment);
    }

}
