package ua.in.quireg.chan.models.presentation;

import android.content.res.Resources;
import android.net.Uri;

import java.util.HashMap;

import ua.in.quireg.chan.R;
import ua.in.quireg.chan.common.Factory;
import ua.in.quireg.chan.common.utils.RegexUtils;
import ua.in.quireg.chan.common.utils.StringUtils;
import ua.in.quireg.chan.common.utils.UriUtils;
import ua.in.quireg.chan.interfaces.IUrlBuilder;
import ua.in.quireg.chan.interfaces.IWebsite;
import ua.in.quireg.chan.models.domain.AttachmentModel;

public class AttachmentInfo {

    private static final HashMap<String, Integer> sDefaultThumbnails;

    private final AttachmentModel mModel;
    private final IWebsite mWebsite;
    private final String mBoardName;
    private final String mThreadNumber;
    private final boolean mIsEmpty;
    private final String mImageUrl;
    private final String mThumbnailUrl;
    private final String mSourceExtension;
    private IUrlBuilder mUrlBuilder;

    static {
        sDefaultThumbnails = new HashMap<>();
        sDefaultThumbnails.put("mp3", R.drawable.page_white_sound_4x);
        sDefaultThumbnails.put("pdf", R.drawable.page_white_acrobat_4x);
        sDefaultThumbnails.put("swf", R.drawable.page_white_flash_4x);
    }

    public AttachmentInfo(AttachmentModel item, IWebsite website, String boardName, String threadNumber) {
        this.mModel = item;
        this.mWebsite = website;
        this.mBoardName = boardName;
        this.mThreadNumber = threadNumber;
        this.mUrlBuilder = this.mWebsite.getUrlBuilder();

        SourceWithThumbnailModel urls = this.getUrls();
        if (urls != null) {
            this.mIsEmpty = false;
            this.mImageUrl = urls.imageUrl;
            this.mThumbnailUrl = urls.thumbnailUrl;
            this.mSourceExtension = this.mImageUrl != null
                    ? RegexUtils.getFileExtension(this.mImageUrl)
                    : null;
        } else {
            this.mIsEmpty = true;
            this.mImageUrl = null;
            this.mThumbnailUrl = null;
            this.mSourceExtension = null;
        }
    }

    public String getThreadUrl() {
        return this.mUrlBuilder.getThreadUrlHtml(this.mBoardName, this.mThreadNumber);
    }

    public String getSourceUrl() {
        if (!this.mIsEmpty) {
            return this.mImageUrl;
        }
        return null;
    }

    public String getImageUrlIfImage() {
        if (this.isImage()) {
            return this.mImageUrl;
        }

        return null;
    }

    public String getSourceExtension() {
        return this.mSourceExtension;
    }

    public boolean isFile() {
        return !StringUtils.isEmpty(this.mSourceExtension);
    }

    public boolean isImage() {
        return this.mImageUrl != null && UriUtils.isImageUri(Uri.parse(this.mImageUrl));
    }

    public boolean isVideo() {
        return this.mImageUrl != null && UriUtils.isVideoUri(Uri.parse(this.mImageUrl));
    }

    public boolean isDisplayableInGallery() {
        return this.isImage() || this.isVideo();
    }

    public String getThumbnailUrl() {
        return this.mThumbnailUrl;
    }

    public int getDefaultThumbnail() {
        Integer resId = AttachmentInfo.sDefaultThumbnails.get(this.mSourceExtension);

        return resId != null ? resId : R.drawable.page_white_4x;
    }

    public boolean isEmpty() {
        return this.mIsEmpty;
    }

    public int getSize() {
        return this.mModel.getImageSize();
    }

    public String getDescription() {
        String result = "";

        if (this.mModel.getImageSize() != 0) {
            result += this.mModel.getImageSize() + Factory.resolve(Resources.class).getString(R.string.data_file_size_measure);

            if ("gif".equalsIgnoreCase(this.mSourceExtension)) {
                result += "\ngif";
            } else if ("webm".equalsIgnoreCase(this.mSourceExtension)) {
                result += "\nwebm";
            } else if ("mp4".equalsIgnoreCase(this.mSourceExtension)) {
                result += "\nmp4";
            }
        }

        return result;
    }

    private SourceWithThumbnailModel getUrls() {
        SourceWithThumbnailModel model = new SourceWithThumbnailModel();

        // Проверяем существование картинки
        String imageUrl = this.mModel.getPath();
        String imageThumbnail = this.mModel.getThumbnailUrl();
        if (!StringUtils.isEmpty(imageUrl)) {
            model.imageUrl = this.mUrlBuilder.getImageUrl(this.mBoardName, imageUrl);
        }
        if (!StringUtils.isEmpty(imageThumbnail)) {
            model.thumbnailUrl = this.mUrlBuilder.getThumbnailUrl(this.mBoardName, imageThumbnail);
        }

        if (model.imageUrl == null && model.thumbnailUrl == null) {
            return null;
        }
        return model;
    }

    private class SourceWithThumbnailModel {

        public String imageUrl;
        public String thumbnailUrl;
    }
}
