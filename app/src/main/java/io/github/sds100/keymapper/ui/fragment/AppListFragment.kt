package io.github.sds100.keymapper.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.bottomappbar.BottomAppBar
import io.github.sds100.keymapper.data.viewmodel.AppListViewModel
import io.github.sds100.keymapper.databinding.FragmentAppListBinding
import io.github.sds100.keymapper.simple
import io.github.sds100.keymapper.util.InjectorUtils

/**
 * Created by sds100 on 22/02/2020.
 */
class AppListFragment : RecyclerViewFragment<FragmentAppListBinding>() {

    companion object {
        const val REQUEST_KEY = "request_app"
        const val EXTRA_APP_MODEL = "extra_app"
        const val SEARCH_STATE_KEY = "key_app_search_state"
    }

    override val progressCallback
        get() = mViewModel

    override var searchStateKey: String? = SEARCH_STATE_KEY
    override var resultData: ResultData? = ResultData(REQUEST_KEY, EXTRA_APP_MODEL)

    override val appBar: BottomAppBar
        get() = binding.appBar

    private val mViewModel: AppListViewModel by viewModels {
        InjectorUtils.provideAppListViewModel(requireContext())
    }

    override fun subscribeList(binding: FragmentAppListBinding) {
        mViewModel.filteredAppModelList.observe(viewLifecycleOwner) { appModelList ->
            binding.epoxyRecyclerView.withModels {
                appModelList.forEach {
                    simple {
                        id(it.packageName)
                        primaryText(it.appName)
                        icon(it.icon)

                        onClick { _ ->
                            selectModel(it)
                        }
                    }
                }
            }
        }
    }

    override fun onSearchQuery(query: String?) {
        mViewModel.searchQuery.value = query
    }

    override fun bind(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAppListBinding.inflate(inflater, container, false).apply {
            progressCallback = this.progressCallback
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }
}