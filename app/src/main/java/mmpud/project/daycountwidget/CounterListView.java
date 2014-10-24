package mmpud.project.daycountwidget;

import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.widget.ListView;

import javax.inject.Inject;

import mortar.Mortar;

/**
 * Created by george on 2014/10/24.
 */
public class CounterListView extends ListView {
    @Inject CounterList.Presenter presenter;

    public CounterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Mortar.inject(context, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // TODO adpter initiate here

        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.dropView(this);
    }

    public void updateAdapter(Cursor cursor) {

    }
}
