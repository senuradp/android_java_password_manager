package com.example.passwordmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccAdapter extends FirebaseRecyclerAdapter <AccountsModel, AccAdapter.myViewHolder> {

    private Context cont;

    public AccAdapter(@NonNull FirebaseRecyclerOptions<AccountsModel> options, Context context ) {
        super(options);
        this.cont = context;
    }

    //populate recycler view with image, social media account name, email and hidden password;
    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView email, password, socialMediaName;
        ImageView btnDelete;
        RelativeLayout accItem;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            socialMediaName = itemView.findViewById(R.id.nameText);
            email = itemView.findViewById(R.id.emailText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            accItem = itemView.findViewById(R.id.accRow);
        }
    }

    //2
    @Override
    protected void onBindViewHolder(@NonNull AccAdapter.myViewHolder holder, int position, @NonNull AccountsModel model) {
        holder.socialMediaName.setText(model.getSocialMediaName());
        holder.email.setText(model.getEmail());
        holder.accItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cont instanceof Home){
                        ((Home)cont).editItem(getRef(position).getKey());
                    }
                }
            });
//        delete social media account
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.socialMediaName.getContext());
                builder.setTitle("Are you sure ? ");
                builder.setMessage("Deleted data cant be undone !");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference("passwordmanager-fc464").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("account")
                                .child(getRef(position).getKey())
                                .removeValue();
                        Toast.makeText(cont, "Account deleted !", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(cont, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    //3
    @NonNull
    @Override
    public AccAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new myViewHolder(view);
    }


}
