package kr.ac.kpu.diyequipmentapplication.community;
import java.io.Serializable;

public class CommunityComment implements Serializable {
    private Boolean commentLike;
    private String comment;
    private String commentNickname;
    private String commentDate;

    public CommunityComment() {
    }

    public CommunityComment(Boolean commentLike, String comment, String commentNickname, String commentDate) {
        this.commentLike = commentLike;
        this.comment = comment;
        this.commentNickname = commentNickname;
        this.commentDate = commentDate;
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
}
