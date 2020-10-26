package de.dasco.mygallery.timeline

import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.dasco.mygallery.R
import de.dasco.mygallery.databinding.FragmentTimelineListBinding

/**
 * A fragment representing a list of Items.
 */
class TimelineFragment : Fragment() {

    private var columnCount = 4

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel: TimelineViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(
            this,
            TimelineViewModel.Factory(activity.application)
        ).get(TimelineViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        println("Added")

        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timeline_list,
            container,
            false
        ) as FragmentTimelineListBinding

        val timelineAdapter = MyMediaItemRecyclerViewAdapter()

        binding.recyclerview.apply {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount).apply {
                    spanSizeLookup = object :GridLayoutManager.SpanSizeLookup(){
                        override fun getSpanSize(position: Int): Int {
                            return if(adapter?.getItemViewType(position) == 1){
                                1
                            }else{
                               spanCount
                            }
                        }

                    }
                }
            }

            adapter = timelineAdapter
        }

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

/*
        viewModel.images.observe(viewLifecycleOwner, {
            println("Submit: $it")
            timelineAdapter.submitList(it)
        })
*/


        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}