package cdld.txg.com.coordinatorlayoutdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StringrvAdapter extends RecyclerView.Adapter {

    private List<String> list;
    private Context mCotext;
    private LayoutInflater inflter;


    public StringrvAdapter(List<String> list, Context mCotext) {
        this.list = list;
        this.mCotext = mCotext;
        inflter = LayoutInflater.from(mCotext);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflter.inflate(R.layout.item_string, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tv_textview.setText(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(list.size() == 0){
            return 1;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_textview;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_textview = itemView.findViewById(R.id.tv_textview);
        }

    }
}
