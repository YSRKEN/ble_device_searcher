package ysrken.ble_device_searcher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView


class BluetoothListAdapter(context: Context, resource: Int, items: MutableList<BluetoothListItem>) : ArrayAdapter<BluetoothListItem>(context, resource, items) {
    private val mResource: Int = resource
    private val mItems: List<BluetoothListItem> = items
    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: mInflater.inflate(mResource, null)
        val item = mItems[position]

        (view.findViewById(R.id.favoriteCheckbox) as CheckBox).isChecked = item.favoriteFlg
        (view.findViewById(R.id.nameTextView) as TextView).text = item.name
        (view.findViewById(R.id.addressTextView) as TextView).text = item.address
        (view.findViewById(R.id.deviceTypeTextView) as TextView).text = item.deviceType

        return view
    }
}