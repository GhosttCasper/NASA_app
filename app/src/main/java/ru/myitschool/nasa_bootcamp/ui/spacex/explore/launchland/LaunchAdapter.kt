package ru.myitschool.nasa_bootcamp.ui.spacex.explore.launchland

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.myitschool.nasa_bootcamp.data.model.LaunchPadModel
import ru.myitschool.nasa_bootcamp.databinding.LaunchPadTemBinding
import java.util.ArrayList

class LaunchAdapter internal constructor(
    context: Context,
    launchModel: ArrayList<LaunchPadModel>,
    // val navController: NavController
) :
    RecyclerView.Adapter<LaunchHolder>() {
    var context: Context
    var launchModels: ArrayList<LaunchPadModel>



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchHolder {
        return LaunchHolder(
            LaunchPadTemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            context
        )
    }

    override fun onBindViewHolder(holder: LaunchHolder, position: Int) {
        val launchModel : LaunchPadModel = launchModels[position]

        holder.binding.nameLaunchPad.text = launchModel.name
        holder.binding.attemptedLaunches.text = "Attempted launches : ${launchModel.location.attempted_launches}"
        holder.binding.vehiclesLaunched.text = "Vehicles launched: ${launchModel.location.vehicles_launched}"
        holder.binding.locationLaunchPad.text = "Located : ${launchModel.location.site_name_long}"
        holder.binding.successfulLaunches.text = "Successful launches : ${launchModel.location.successful_launches}"
        holder.binding.descriptionLand.text = "${launchModel.location.details}"

    }

    override fun getItemCount(): Int {
        return launchModels.size
    }

    fun update(modelList: ArrayList<LaunchPadModel>) {
        launchModels = modelList
        notifyDataSetChanged()
    }

    init {
        this.context = context
        this.launchModels = launchModel
    }
}