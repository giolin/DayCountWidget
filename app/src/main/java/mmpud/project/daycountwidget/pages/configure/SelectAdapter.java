package mmpud.project.daycountwidget.pages.configure;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mmpud.project.daycountwidget.R;
import timber.log.Timber;

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int[] colors;
    private OnItemClickListener onItemClickListener;

    SelectAdapter(int[] colors, OnItemClickListener onItemClickListener) {
        this.colors = new int[colors.length + 1];
        System.arraycopy(colors, 0, this.colors, 0, colors.length);
        for (int color : this.colors) {
            Timber.d("color %d", color);
        }
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.round_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v.getId());
            }
        });
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setId(position);
        GradientDrawable gradientDrawable = (GradientDrawable) holder.itemView.getBackground();
        Timber.d("position %d", position);
        gradientDrawable.clearColorFilter();
        if (isLastItem(position)) {
            Timber.d("last item rainbow");
            // rainbow color for user to define
            gradientDrawable.setColors(new int[]{Color.RED, Color.MAGENTA, Color.BLUE,
                    Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});
            gradientDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
        } else {
            Timber.d("set color to %d", colors[position]);
            gradientDrawable.setColor(colors[position]);
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    boolean isLastItem(int position) {
        return position == colors.length - 1;
    }

    int getColor(int position) {
        return colors[position];
    }

}