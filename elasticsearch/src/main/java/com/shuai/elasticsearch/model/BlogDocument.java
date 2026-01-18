package com.shuai.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * 博客文档实体类
 *
 * 功能说明
 * ----------
 * 用于演示 Elasticsearch 文档操作的数据模型。
 *
 * 字段说明
 * ----------
 *   - id: 文档唯一标识
 *   - title: 标题（text 类型，支持分词）
 *   - content: 内容（text 类型，支持分词）
 *   - author: 作者（keyword 类型，精确匹配）
 *   - tags: 标签列表（keyword 类型数组）
 *   - views: 浏览次数（long 类型，用于聚合）
 *   - status: 状态（published/draft）
 *   - createTime: 创建时间
 *
 * @author Shuai
 * @version 1.0
 */
public class BlogDocument {

    private String id;
    private String title;
    private String content;
    private String author;

    @JsonProperty("tags")
    private List<String> tags;

    @JsonProperty("views")
    private Integer views;

    @JsonProperty("status")
    private String status;

    @JsonProperty("createTime")
    private String createTime;

    // 构造函数
    public BlogDocument() {
    }

    public BlogDocument(String id, String title, String content, String author,
                        List<String> tags, Integer views, String status, String createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
        this.views = views;
        this.status = status;
        this.createTime = createTime;
    }

    // Builder 模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final BlogDocument doc = new BlogDocument();

        public Builder id(String id) {
            doc.id = id;
            return this;
        }

        public Builder title(String title) {
            doc.title = title;
            return this;
        }

        public Builder content(String content) {
            doc.content = content;
            return this;
        }

        public Builder author(String author) {
            doc.author = author;
            return this;
        }

        public Builder tags(List<String> tags) {
            doc.tags = tags;
            return this;
        }

        public Builder views(Integer views) {
            doc.views = views;
            return this;
        }

        public Builder status(String status) {
            doc.status = status;
            return this;
        }

        public Builder createTime(String createTime) {
            doc.createTime = createTime;
            return this;
        }

        public BlogDocument build() {
            return doc;
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogDocument that = (BlogDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BlogDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", views=" + views +
                ", status='" + status + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
