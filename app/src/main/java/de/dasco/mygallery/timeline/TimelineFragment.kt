package de.dasco.mygallery.timeline

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import de.dasco.mygallery.MyItemDetailsLookup
import de.dasco.mygallery.MyItemKeyProvider
import de.dasco.mygallery.R
import de.dasco.mygallery.databinding.FragmentTimelineListBinding
import de.dasco.mygallery.models.HeaderItem
import kotlinx.android.synthetic.main.fragment_timeline_list.*
import kotlinx.android.synthetic.main.header_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*

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

    private var actionMode: ActionMode? = null
    private var tracker: SelectionTracker<Long>? = null


    private val actionModeCallback = object : ActionMode.Callback {
        // Called when the action mode is created; startActionMode() was called
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            // Inflate a menu resource providing context menu items
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.context_menu, menu)
            return true
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.menu_share -> {
//                    shareCurrentItem()
                    mode.finish() // Action picked, so close the CAB
                    true
                }
                else -> false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            tracker?.clearSelection()
            actionMode = null
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
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (adapter?.getItemViewType(position) == 1) {
                                1
                            } else {
                                spanCount
                            }
                        }
                    }
                }
            }

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.set(4, 4, 4, 4)
//                    super.getItemOffsets(outRect, view, parent, state)
                }
            })

            setItemViewCacheSize(20)

            adapter = timelineAdapter
        }

        tracker = SelectionTracker.Builder(
            "mySelection",
            binding.recyclerview,
            MyItemKeyProvider(timelineAdapter),
            MyItemDetailsLookup(binding.recyclerview),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        timelineAdapter.tracker = tracker

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {

                fun headerSelect(header: HeaderItem): Boolean {

                    header.children.forEach { child ->
                        if (!tracker?.isSelected(child)!!) {
                            return false
                        }
                    }

                    return true
                }

                override fun onItemStateChanged(key: Long, selected: Boolean) {

                    val item = timelineAdapter.currentList.find { dataItem -> dataItem.id == key }
                    if (item is DataItem.ImageItem) {
                        if (!selected) {
                            tracker?.deselect(item.image.header)
                        }
                        val headerItem = timelineAdapter.currentList.find { dataItem -> dataItem.id == item.image.header } as DataItem.Header
                        if(headerSelect(headerItem.header)){
                            tracker?.select(item.image.header)
                        }
                    }

                    if (item is DataItem.Header) {
//                        println("${ tracker?.selection. item.header.children }")
                        if (selected) {
                            tracker?.setItemsSelected(item.header.children, true)
                        } else {

                            if (headerSelect(item.header)) {
                                tracker?.setItemsSelected(
                                    item.header.children,
                                    false
                                )
                            }

                        }
                    }


                    super.onItemStateChanged(key, selected)
                }

                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = tracker?.selection!!.size()

                    if (items == 0) {

                        actionMode?.finish()

                        recyclerview.children.forEach {
                            it.checkBoxContainer?.transitionToStart()
                            it.headerContainer?.transitionToStart()
                            it.imageContainer?.setTransitionDuration(1)
                            it.imageContainer?.transitionToStart()
                            it.imageContainer?.setTransitionDuration(100)
                        }

                    } else {
                        when (actionMode) {
                            null -> {
                                // Start the CAB using the ActionMode.Callback defined above
                                actionMode = activity?.startActionMode(actionModeCallback)

                                recyclerview.children.forEach {
                                    it.checkBoxContainer?.transitionToEnd()
                                    it.headerContainer?.transitionToEnd()
                                }

                            }
                        }

                        tracker?.selection!!


                        val imageCounter =
                            tracker?.selection!!.filter {
                                timelineAdapter.currentList.find { dataItem -> dataItem.id == it } is DataItem.ImageItem
                            }.size
//                        println("Counter: ${timelineAdapter.currentList.get(0) is DataItem.Header}")
                        println("Counter: $imageCounter")
                        tracker?.selection!!.forEach {

                        }

                        actionMode?.title = imageCounter.toString()
                    }
                }
            })

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        return binding.root
    }

}