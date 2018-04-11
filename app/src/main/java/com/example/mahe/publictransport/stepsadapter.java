package com.example.mahe.publictransport;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mahe on 6/24/2017.
 */

public class stepsadapter extends RecyclerView.Adapter<stepsadapter.stepholder> {
    Context mContext;
    ArrayList<String> steps=new ArrayList<>();

    public stepsadapter(Context context)
     {
         mContext=context;
     }

    @Override
    public stepholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.directionsresource,parent,false);
        view.setFocusable(true);
        return new stepholder(view);



    }

    @Override
    public void onBindViewHolder(stepholder holder, int position) {
        String step=steps.get(position);
        Log.i("step",step);
        holder.steptext.setText(step);

    }

    @Override
    public int getItemCount() {
        if(steps.size()==0 || steps==null)
        {
        return 0;}
        else
        {
            return steps.size();
        }
    }
public void swaplist(ArrayList<String> list)
{
    steps=list;
     notifyDataSetChanged();

}


    public class stepholder extends RecyclerView.ViewHolder{
      CardView step;
        TextView steptext;

        public stepholder(View itemView) {
            super(itemView);
            step=(CardView) itemView.findViewById(R.id.Step);
             steptext=(TextView)itemView.findViewById(R.id.steptext);

        }
    }


}
