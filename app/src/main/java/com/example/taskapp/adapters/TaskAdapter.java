package com.example.taskapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskapp.R;
import com.example.taskapp.models.TaskModel;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<TaskModel> tasksDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView task_Name;
        private final TextView task_Date;
        private final TextView task_Time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            task_Name = (TextView) itemView.findViewById(R.id.dashboard_item_taskName);
            task_Date = (TextView) itemView.findViewById(R.id.dashboard_item_taskDate);
            task_Time = (TextView) itemView.findViewById(R.id.dashboard_item_taskTime);
        }

        public TextView getTask_NameView() {
            return task_Name;
        }

        public TextView getTask_DateView() {
            return task_Date;
        }

        public TextView getTask_TimeView() {
            return task_Time;
        }
    }

    public TaskAdapter(List<TaskModel> dataSet) {
        tasksDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTask_NameView().setText(tasksDataSet.get(position).getTaskName());
        holder.getTask_DateView().setText(tasksDataSet.get(position).getTaskDate());
        holder.getTask_TimeView().setText(tasksDataSet.get(position).getTaskTime());
    }

    @Override
    public int getItemCount() {
        return tasksDataSet.size();
    }

}
