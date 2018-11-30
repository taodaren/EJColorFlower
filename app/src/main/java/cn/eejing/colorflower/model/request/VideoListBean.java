package cn.eejing.colorflower.model.request;

import java.util.List;

public class VideoListBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"title":"彩花机齐喷效果","url":"http://eejing-video.oss-cn-shanghai.aliyuncs.com/Ej_obiOs5.mp4","thumbnail_url":"http://eejing-video.oss-cn-shanghai.aliyuncs.com/Ej_obiOs5.mp4?x-oss-process=video/snapshot,t_12000,f_jpg,w_960,h_540,m_fast"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 彩花机齐喷效果
         * url : http://eejing-video.oss-cn-shanghai.aliyuncs.com/Ej_obiOs5.mp4
         * thumbnail_url : http://eejing-video.oss-cn-shanghai.aliyuncs.com/Ej_obiOs5.mp4?x-oss-process=video/snapshot,t_12000,f_jpg,w_960,h_540,m_fast
         */

        private String title;
        private String url;
        private String thumbnail_url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumbnail_url() {
            return thumbnail_url;
        }

        public void setThumbnail_url(String thumbnail_url) {
            this.thumbnail_url = thumbnail_url;
        }
    }
}
