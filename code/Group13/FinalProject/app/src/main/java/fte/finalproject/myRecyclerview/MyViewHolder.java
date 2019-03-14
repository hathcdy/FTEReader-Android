package fte.finalproject.myRecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    private View view;
    private boolean isNormalType;

    public void setNormalTypeType(boolean type) {
        this.isNormalType = type;
    }

    public boolean isNormalType() {
        return isNormalType;
    }

    public MyViewHolder(Context _context, View _view, ViewGroup _viewGroup){
        super(_view);
        view = _view;
        views = new SparseArray<View>();
    }

    public static MyViewHolder get(Context _context, ViewGroup _viewGroup, int _layoutId) {
        View _view = LayoutInflater.from(_context).inflate(_layoutId, _viewGroup, false);
        MyViewHolder holder = new MyViewHolder(_context, _view, _viewGroup);
        return holder;
    }

    public <T extends View> T getView(int _viewId) {
        View _view = views.get(_viewId);
        if (_view == null) {
            // 创建view
            _view = view.findViewById(_viewId);
            // 将view存入views
            views.put(_viewId, _view);
        }
        return (T)_view;
    }

}

