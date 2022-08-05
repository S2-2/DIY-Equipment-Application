package kr.ac.kpu.diyequipmentapplication.community;
import java.io.Serializable;

public class CommunityComment implements Serializable {
    private Boolean commentLike;
    private String comment;
    private String commentNickname;
    private String commentDate;
    private String commentHostNickname;
    private String communityHostId;

    public CommunityComment() {
    }

    public CommunityComment(Boolean commentLike, String comment, String commentNickname, String commentDate, String commentHostNickname, String communityHostId) {
        this.commentLike = commentLike;
        this.comment = comment;
        this.commentNickname = commentNickname;
        this.commentDate = commentDate;
        this.commentHostNickname = commentHostNickname;
        this.communityHostId = communityHostId;
    }

    public Boolean getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(Boolean commentLike) {
        this.commentLike = commentLike;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentNickname() {
        return commentNickname;
    }

    public void setCommentNickname(String commentNickname) {
        this.commentNickname = commentNickname;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentHostNickname() {
        return commentHostNickname;
    }

    public void setCommentHostNickname(String commentHostNickname) {
        this.commentHostNickname = commentHostNickname;
    }

    public String getCommunityHostId() {
        return communityHostId;
    }

    public void setCommunityHostId(String communityHostId) {
        this.communityHostId = communityHostId;
    }
}
