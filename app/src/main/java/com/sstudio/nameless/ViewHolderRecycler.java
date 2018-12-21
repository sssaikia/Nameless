package com.sstudio.nameless;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Alan on 9/22/2017.
 */

public class ViewHolderRecycler extends RecyclerView.ViewHolder {

    String itemText;
    TextView textView;
    public ViewHolderRecycler(View itemView) {
        super(itemView);
        this.textView=itemView.findViewById(R.id.pallete);
        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos=getLayoutPosition();
                Log.d("item ::   ",textView.getText().toString()+"");
                itemText=textView.getText().toString();
            }
        });*/
    }

    public TextView getTextView() {
        return textView;
    }
    public String getItemText(){
        return itemText;
    }
}
