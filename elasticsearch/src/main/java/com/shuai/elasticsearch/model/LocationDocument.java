package com.shuai.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * 地理位置文档实体类
 *
 * 功能说明
 * ----------
 * 用于演示 Elasticsearch 地理查询的数据模型。
 *
 * 字段说明
 * ----------
 *   - id: 文档唯一标识
 *   - name: 地点名称
 *   - location: 经纬度坐标 (geo_point)
 *   - area: 区域形状 (geo_shape)
 *   - type: 地点类型
 *
 * @author Shuai
 * @version 1.0
 */
public class LocationDocument {

    private String id;
    private String name;

    @JsonProperty("location")
    private GeoPoint location;

    @JsonProperty("area")
    private GeoShape area;

    @JsonProperty("type")
    private String type;

    // 构造函数
    public LocationDocument() {
    }

    public LocationDocument(String id, String name, GeoPoint location, GeoShape area, String type) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.area = area;
        this.type = type;
    }

    // Builder 模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LocationDocument doc = new LocationDocument();

        public Builder id(String id) {
            doc.id = id;
            return this;
        }

        public Builder name(String name) {
            doc.name = name;
            return this;
        }

        public Builder location(GeoPoint location) {
            doc.location = location;
            return this;
        }

        public Builder area(GeoShape area) {
            doc.area = area;
            return this;
        }

        public Builder type(String type) {
            doc.type = type;
            return this;
        }

        public LocationDocument build() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public GeoShape getArea() {
        return area;
    }

    public void setArea(GeoShape area) {
        this.area = area;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDocument that = (LocationDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LocationDocument{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", type='" + type + '\'' +
                '}';
    }

    /**
     * 地理坐标点
     */
    public static class GeoPoint {
        private Double lat;
        private Double lon;

        public GeoPoint() {
        }

        public GeoPoint(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final GeoPoint point = new GeoPoint();

            public Builder lat(Double lat) {
                point.lat = lat;
                return this;
            }

            public Builder lon(Double lon) {
                point.lon = lon;
                return this;
            }

            public GeoPoint build() {
                return point;
            }
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        @Override
        public String toString() {
            return "GeoPoint{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }

    /**
     * 地理形状
     */
    public static class GeoShape {
        private String type;
        private List<List<List<Double>>> coordinates;

        public GeoShape() {
        }

        public GeoShape(String type, List<List<List<Double>>> coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final GeoShape shape = new GeoShape();

            public Builder type(String type) {
                shape.type = type;
                return this;
            }

            public Builder coordinates(List<List<List<Double>>> coordinates) {
                shape.coordinates = coordinates;
                return this;
            }

            public GeoShape build() {
                return shape;
            }
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<List<List<Double>>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<List<Double>>> coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "GeoShape{" +
                    "type='" + type + '\'' +
                    ", coordinates=" + coordinates +
                    '}';
        }
    }
}
