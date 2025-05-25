package com.example.gmh_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmh_app.Activities.ProfileActivity;
import com.example.gmh_app.R;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    private final List<ProfileActivity.Certificate> certificates;

    public CertificateAdapter(List<ProfileActivity.Certificate> certificates) {
        this.certificates = certificates;
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        ProfileActivity.Certificate cert = certificates.get(position);
        holder.title.setText(cert.title);
        holder.issuer.setText(cert.issuer);
        holder.date.setText(cert.date);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(cert.fileUri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Cannot open certificate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return certificates.size();
    }

    static class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView title, issuer, date;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.certTitle);
            issuer = itemView.findViewById(R.id.certIssuer);
            date = itemView.findViewById(R.id.certDate);
        }
    }
}
