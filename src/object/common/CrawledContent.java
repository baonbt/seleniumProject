/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.common;

import object.dto.*;

/**
 *
 * @author baonb
 */
public class CrawledContent {
    String postId;
    String postContent;
    String imgStoredDir;

    public String getPostId() {
        return postId;
    }

    public String getImgStoredDir() {
        return imgStoredDir;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setImgStoredDir(String imgStoredDir) {
        this.imgStoredDir = imgStoredDir;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    
}
