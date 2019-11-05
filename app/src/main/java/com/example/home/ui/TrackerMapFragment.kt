/*
package com.example.home.ui


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.home.R
import com.example.home.service.TrackerForegroundService

*/
/**
 * A simple [Fragment] subclass.
 *//*

class TrackerMapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracker_map_fagment, container, false)
    }

    override fun onStart() {
        //binding this fragment to service
        activity!!.bindService(Intent(activity, TrackerForegroundService::class.java), connection, Context.BIND_AUTO_CREATE)
        super.onStart()
    }

    override fun onStop() {
        //un Bind fragment from service
        activity!!.unbindService(connection)
        super.onStop()
    }

    */
/**
     * Defines callbacks for service binding, passed to bindService()
     *//*

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TrackerForegroundService.TrackerBinder


        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }

}
*/
