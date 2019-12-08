package com.socialgaming.appsclub.Util;

public class Events {

    // Event used to send message from sub category notify.
    public static class SubCatNotify {
        private String View, TotalLike, alreadyLike, type;
        private int position;

        public SubCatNotify(String view, String totalLike, String alreadyLike, String type, int position) {
            View = view;
            TotalLike = totalLike;
            this.alreadyLike = alreadyLike;
            this.type = type;
            this.position = position;
        }

        public String getView() {
            return View;
        }

        public void setView(String view) {
            View = view;
        }

        public String getTotalLike() {
            return TotalLike;
        }

        public void setTotalLike(String totalLike) {
            TotalLike = totalLike;
        }

        public String getAlreadyLike() {
            return alreadyLike;
        }

        public void setAlreadyLike(String alreadyLike) {
            this.alreadyLike = alreadyLike;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }


    // Event used to send message from home sub category notify.
    public static class HomeSubCatNotify {
        private String View, TotalLike, alreadyLike, type;
        private int position;

        public HomeSubCatNotify(String view, String totalLike, String alreadyLike, String type, int position) {
            View = view;
            TotalLike = totalLike;
            this.alreadyLike = alreadyLike;
            this.type = type;
            this.position = position;
        }

        public String getView() {
            return View;
        }

        public void setView(String view) {
            View = view;
        }

        public String getTotalLike() {
            return TotalLike;
        }

        public void setTotalLike(String totalLike) {
            TotalLike = totalLike;
        }

        public String getAlreadyLike() {
            return alreadyLike;
        }

        public void setAlreadyLike(String alreadyLike) {
            this.alreadyLike = alreadyLike;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    // Event used to send message from slider data notify.
    public static class FragmentSliderNotify {
        private String slider_notify;
        private int position;

        public FragmentSliderNotify(String slider_notify, int position) {
            this.slider_notify = slider_notify;
            this.position = position;
        }

        public String getSlider_notify() {
            return slider_notify;
        }

        public int getPosition() {
            return position;
        }
    }

    // Event used to send message from stop player.
    public static class StopPlay {
        private String play;

        public StopPlay(String play) {
            this.play = play;
        }

        public String getPlay() {
            return play;
        }
    }

    // Event used to favourite or not check notify.
    public static class FavouriteNotify {
        private String id, tag;

        public FavouriteNotify(String id, String tag) {
            this.id = id;
            this.tag = tag;
        }

        public String getId() {
            return id;
        }

        public String getTag() {
            return tag;
        }
    }

    // Event used to send message from reward data notify.
    public static class RewardNotify {
        private String reward;

        public RewardNotify(String reward) {
            this.reward = reward;
        }

        public String getReward() {
            return reward;
        }
    }

    // Event used to send message from comment notify.
    public static class Comment {
        private String comment_id, user_id, user_name, user_image, video_id, comment_text, comment_date, total_comment;

        public Comment(String comment_id, String user_id, String user_name, String user_image, String video_id, String comment_text, String comment_date, String total_comment) {
            this.comment_id = comment_id;
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_image = user_image;
            this.video_id = video_id;
            this.comment_text = comment_text;
            this.comment_date = comment_date;
            this.total_comment = total_comment;
        }

        public String getComment_id() {
            return comment_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public String getUser_image() {
            return user_image;
        }

        public String getVideo_id() {
            return video_id;
        }

        public String getComment_text() {
            return comment_text;
        }

        public String getComment_date() {
            return comment_date;
        }

        public String getTotal_comment() {
            return total_comment;
        }
    }

    //Event used to send message from total comment
    public static class TotalComment {

        private String total_comment, video_id, comment_id, type;

        public TotalComment(String total_comment, String video_id, String comment_id, String type) {
            this.total_comment = total_comment;
            this.video_id = video_id;
            this.comment_id = comment_id;
            this.type = type;
        }

        public String getTotal_comment() {
            return total_comment;
        }

        public String getVideo_id() {
            return video_id;
        }

        public String getComment_id() {
            return comment_id;
        }

        public String getType() {
            return type;
        }
    }

    // Event used to send message from my video notify.
    public static class MyVideoView {
        private String View, TotalLike, alreadyLike, type;
        private int position;

        public MyVideoView(String view, String totalLike, String alreadyLike, String type, int position) {
            View = view;
            TotalLike = totalLike;
            this.alreadyLike = alreadyLike;
            this.type = type;
            this.position = position;
        }

        public String getView() {
            return View;
        }

        public void setView(String view) {
            View = view;
        }

        public String getTotalLike() {
            return TotalLike;
        }

        public void setTotalLike(String totalLike) {
            TotalLike = totalLike;
        }

        public String getAlreadyLike() {
            return alreadyLike;
        }

        public void setAlreadyLike(String alreadyLike) {
            this.alreadyLike = alreadyLike;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    // Event used to send message from login notify.
    public static class Login {
        private String login;

        public Login(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    // Event used to image status notify.
    public static class ImageStatusNotify {
        private String imageNotify;

        public ImageStatusNotify(String imageNotify) {
            this.imageNotify = imageNotify;
        }

        public String getImageNotify() {
            return imageNotify;
        }

    }

    // Event used to video status notify.
    public static class VideoStatusNotify {
        private String videoNotify;

        public VideoStatusNotify(String videoNotify) {
            this.videoNotify = videoNotify;
        }

        public String getVideoNotify() {
            return videoNotify;
        }

    }

    // Event used to related fragment notify.
    public static class RelatedFragmentNotify {

        private String related_View, related_TotalLike, related_alreadyLike, type, video_id;
        private int position;

        public RelatedFragmentNotify(String related_View, String related_TotalLike, String related_alreadyLike, String type, String video_id, int position) {
            this.related_View = related_View;
            this.related_TotalLike = related_TotalLike;
            this.related_alreadyLike = related_alreadyLike;
            this.type = type;
            this.video_id = video_id;
            this.position = position;
        }

        public String getRelated_View() {
            return related_View;
        }

        public String getRelated_TotalLike() {
            return related_TotalLike;
        }

        public String getRelated_alreadyLike() {
            return related_alreadyLike;
        }

        public String getType() {
            return type;
        }

        public String getVideo_id() {
            return video_id;
        }

        public int getPosition() {
            return position;
        }
    }

    // Event used to search fragment notify.
    public static class SearchFragmentNotify {

        private String search_View, search_TotalLike, search_alreadyLike, type;
        private int position;

        public SearchFragmentNotify(String search_View, String search_TotalLike, String search_alreadyLike, String type, int position) {
            this.search_View = search_View;
            this.search_TotalLike = search_TotalLike;
            this.search_alreadyLike = search_alreadyLike;
            this.type = type;
            this.position = position;
        }

        public String getSearch_View() {
            return search_View;
        }

        public String getSearch_TotalLike() {
            return search_TotalLike;
        }

        public String getSearch_alreadyLike() {
            return search_alreadyLike;
        }

        public String getType() {
            return type;
        }

        public int getPosition() {
            return position;
        }
    }

    // Event used to full screen notify.
    public static class FullScreenNotify {
        private boolean fullscreen;

        public FullScreenNotify(boolean fullscreen) {
            this.fullscreen = fullscreen;
        }

        public boolean isFullscreen() {
            return fullscreen;
        }
    }

    //Event used to select notify.
    public static class Select {

        private String string;

        public Select(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

}
