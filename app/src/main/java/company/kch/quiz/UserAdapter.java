package company.kch.quiz;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kirillchayevskiy on 05.12.2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<UserModel> list;

    public UserAdapter(List<UserModel> list) {
        this.list = list;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserModel user = list.get(position);

        holder.textName.setText(user.name);
        holder.textScore.setText(String.valueOf(user.score));


        holder.textNum.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textNum, textName, textScore;

        public UserViewHolder(View itemView) {
            super(itemView);

            textNum = itemView.findViewById(R.id.textNum);
            textName = itemView.findViewById(R.id.textName);
            textScore = itemView.findViewById(R.id.textScore);
        }
    }
}
