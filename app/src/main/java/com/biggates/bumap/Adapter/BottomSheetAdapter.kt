package com.biggates.bumap.Adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.biggates.bumap.Model.BuildingSubInfo
import com.biggates.bumap.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.bottomsheet_layout_home.view.*

@RequiresApi(Build.VERSION_CODES.M)
class BottomSheetAdapter(private var mContext : Context, private var placeList : ArrayList<MutableMap.MutableEntry<String,BuildingSubInfo>>, private var category : String)
    :RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>(){

    inner class ViewHolder(@NonNull item : View) : RecyclerView.ViewHolder(item){
        var subInfoTitle = item.sub_info_title
        var subInfoImage = item.sub_info_image
        var layout = item.sub_info_wrapper

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetAdapter.ViewHolder {
        var view = LayoutInflater.from(mContext).inflate(R.layout.bottomsheet_layout_home,parent,false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: BottomSheetAdapter.ViewHolder, position: Int) {
        var place = placeList[position]
        var title = "${place.value.name}  ${category}"
        var spannableString = SpannableString(title)
        holder.subInfoImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
        spannableString.setSpan( ForegroundColorSpan(mContext.resources.getColor(R.color.browser_actions_title_color,null)), title.lastIndexOf(" ")+1, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan( RelativeSizeSpan(0.75f), title.lastIndexOf(" ")+1, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.subInfoTitle.text = spannableString
        if(place.value.img != "") {
            Glide.with(mContext)
                .load(place.value.img)
                .override(holder.subInfoImage.width,holder.subInfoImage.height)
                .placeholder(R.drawable.progress_bar_mini)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.subInfoImage.scaleType = ImageView.ScaleType.FIT_XY
                        return false
                    }

                })
                .into(holder.subInfoImage)




        }else{
            holder.subInfoImage.scaleType = ImageView.ScaleType.FIT_XY
            holder.subInfoImage.setImageResource(R.drawable.loadingimg)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}