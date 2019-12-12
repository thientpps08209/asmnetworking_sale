package com.example.ps08209_ps08304_ASMNETWORKING.utilities;

import android.support.v7.util.DiffUtil;

import com.example.ps08209_ps08304_ASMNETWORKING.model.Products;

import java.util.List;

public class ProductDiffCallback extends DiffUtil.Callback {
    private List<Products> oldList;
    private List<Products> newList;

    public ProductDiffCallback(List<Products> oldList, List<Products> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        /**
         * Nhớ thêm mỗi sản phẩm có một mã số khác nhau, và mã số đó
         * lấy bằng hàm getMaSp()
         */
        return oldList.get(oldItemPosition).getMaSp()
                == newList.get(newItemPosition).getMaSp();
    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //Trả về true nếu dữ liệu giống nhau ở vị mới / cũ
        //Ở đây so sánh tên sản phẩm
        return oldList.get(oldItemPosition).getTenSp()
                == newList.get(newItemPosition).getTenSp();
    }
}
