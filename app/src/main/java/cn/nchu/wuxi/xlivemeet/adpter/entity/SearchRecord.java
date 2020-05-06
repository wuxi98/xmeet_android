/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.nchu.wuxi.xlivemeet.adpter.entity;




public class SearchRecord {


    private long Id;
    /**
     * 搜索内容
     */

    private String content;

    /**
     * 搜索时间
     */

    private long time;


    public long getId() {
        return Id;
    }

    public SearchRecord setId(long id) {
        Id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SearchRecord setContent(String content) {
        this.content = content;
        return this;
    }

    public long getTime() {
        return time;
    }

    public SearchRecord setTime(long time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return "QueryRecord{" +
                "Id=" + Id +
                ", content='" + content + '\'' +
                ", time=" + time +
                '}';
    }
}
