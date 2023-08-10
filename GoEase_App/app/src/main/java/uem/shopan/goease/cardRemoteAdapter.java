package uem.shopan.goease;

//cardRemoteModel

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

public class cardRemoteAdapter extends RecyclerView.Adapter<cardRemoteAdapter.Viewholder> {

    private Context context;
    private ArrayList<cardRemoteModel> cardRemoteModelArrayList;

    // Constructor
    public cardRemoteAdapter(Context context, ArrayList<cardRemoteModel> cardRemoteModelArrayList) {
        this.context = context;
        this.cardRemoteModelArrayList = cardRemoteModelArrayList;
    }

    @NonNull
    @Override
    public cardRemoteAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_remote_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull cardRemoteAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        cardRemoteModel model = cardRemoteModelArrayList.get(position);
        holder.deviceName.setText(model.getRemote_name());
        holder.deviceID.setText(model.getRemote_id());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return cardRemoteModelArrayList.size();
    }


    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView deviceName, deviceID;

        public Viewholder(@NonNull View itemView) {
            super(itemView);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //Toast.makeText(context.getApplicationContext(),getBindingAdapterPosition(),Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "Delete current device", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    cardRemoteModel model = cardRemoteModelArrayList.get(getBindingAdapterPosition());

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

                    cardRemoteModel model = cardRemoteModelArrayList.get(getBindingAdapterPosition());

                    //Toast.makeText(context.getApplicationContext(),model.getDevice_name(),Toast.LENGTH_SHORT).show();

                    //getBindingAdapterPosition()
                    //getLayoutPosition()
                    Intent i = new Intent(v.getContext(),Details.class);
                    i.putExtra("page","2");
                    i.putExtra("name",model.getDevice_name()); //getBindingAdapterPosition()
                    i.putExtra("id",model.getDevice_id()); //getBindingAdapterPosition()
                    i.putExtra("key",model.getDevice_key()); //getBindingAdapterPosition()
                    i.putExtra("remote_name",model.getRemote_name()); //getBindingAdapterPosition()
                    i.putExtra("remote_id",model.getRemote_id()); //getBindingAdapterPosition()
                    v.getContext().startActivity(i);

                }
            });

            deviceName = itemView.findViewById(R.id.idDeviceName);
            deviceID = itemView.findViewById(R.id.idDeviceID);
        }
    }

}
