package mmpud.project.daycountwidget.misc;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Interface definition for a callback to be invoked when an item in this
 * {@link RecyclerView} has been clicked.
 */
public interface RecyclerViewOnItemLongClickListener {

    boolean onItemLongClick(RecyclerView.Adapter adapter, View view, int position);

}
