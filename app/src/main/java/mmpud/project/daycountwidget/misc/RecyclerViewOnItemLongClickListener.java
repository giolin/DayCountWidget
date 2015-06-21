package mmpud.project.daycountwidget.misc;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Interface definition for a callback to be invoked when an item in this
 * {@link android.support.v7.widget.RecyclerView} has been clicked.
 */
public interface RecyclerViewOnItemLongClickListener {

    boolean onItemLongClick(RecyclerView.Adapter adapter, View view, int position);

}
