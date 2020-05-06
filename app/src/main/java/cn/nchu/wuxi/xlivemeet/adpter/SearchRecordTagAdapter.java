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

package cn.nchu.wuxi.xlivemeet.adpter;

import androidx.annotation.NonNull;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

import cn.nchu.wuxi.xlivemeet.R;
import cn.nchu.wuxi.xlivemeet.adpter.entity.SearchRecord;


public class SearchRecordTagAdapter extends BaseRecyclerAdapter<SearchRecord> {

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_search_record_tag_item;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, SearchRecord item) {
        if (item != null) {
            holder.text(R.id.tv_tag, item.getContent());
        }
    }
}
