package uem.shopan.goease;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.Viewholder> {

    private Context context;
    private ArrayList<cardModel> cardModelArrayList;

    // Constructor
    public cardAdapter(Context context, ArrayList<cardModel> cardModelArrayList) {
        this.context = context;
        this.cardModelArrayList = cardModelArrayList;
    }

    @NonNull
    @Override
    public cardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cardAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        cardModel model = cardModelArrayList.get(position);
        holder.courseNameTV.setText(model.getDevice_name());
        holder.courseRatingTV.setText(model.getDevice_id());
        holder.courseIV.setImageResource(model.getDevice_icon());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return cardModelArrayList.size();
    }


    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView courseIV;
        private TextView courseNameTV, courseRatingTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(context.getApplicationContext(),getBindingAdapterPosition(),Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "Delete current device", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    cardModel model = cardModelArrayList.get(getBindingAdapterPosition());

                    //Toast.makeText(context.getApplicationContext(),model.getDevice_name(),Toast.LENGTH_SHORT).show();

                    //getBindingAdapterPosition()
                    //getLayoutPosition()
                    //Intent i = new Intent(v.getContext(),Details.class);
                    //i.putExtra("name",model.getDevice_name()); //getBindingAdapterPosition()
                    //i.putExtra("id",model.getDevice_id()); //getBindingAdapterPosition()
                    //i.putExtra("key",model.getDevice_key()); //getBindingAdapterPosition()
                    //v.getContext().startActivity(i);
                    return true;
                }

            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context.getApplicationContext(),getBindingAdapterPosition(),Toast.LENGTH_SHORT).show();

                    cardModel model = cardModelArrayList.get(getBindingAdapterPosition());

                    //Toast.makeText(context.getApplicationContext(),model.getDevice_name(),Toast.LENGTH_SHORT).show();

                    //getBindingAdapterPosition()
                    //getLayoutPosition()
                    Intent i = new Intent(v.getContext(),Details.class);
                    i.putExtra("page","1");
                    i.putExtra("name",model.getDevice_name()); //getBindingAdapterPosition()
                    i.putExtra("id",model.getDevice_id()); //getBindingAdapterPosition()
                    i.putExtra("key",model.getDevice_key()); //getBindingAdapterPosition()
                    v.getContext().startActivity(i);

                }
            });


            courseIV = itemView.findViewById(R.id.idIVCourseImage);
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);
            courseRatingTV = itemView.findViewById(R.id.idTVCourseRating);
        }
    }

}
