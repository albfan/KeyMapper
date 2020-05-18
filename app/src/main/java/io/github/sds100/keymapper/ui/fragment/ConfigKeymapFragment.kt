package io.github.sds100.keymapper.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.tabs.TabLayoutMediator
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.data.viewmodel.ConfigKeymapViewModel
import io.github.sds100.keymapper.databinding.FragmentConfigKeymapBinding
import io.github.sds100.keymapper.service.MyAccessibilityService
import io.github.sds100.keymapper.ui.adapter.ConfigKeymapPagerAdapter
import io.github.sds100.keymapper.util.*
import splitties.snackbar.snack

/**
 * Created by sds100 on 19/02/2020.
 */
class ConfigKeymapFragment : Fragment() {
    private val mArgs by navArgs<ConfigKeymapFragmentArgs>()

    private val mViewModel: ConfigKeymapViewModel by navGraphViewModels(R.id.nav_config_keymap) {
        InjectorUtils.provideConfigKeymapViewModel(requireContext(), mArgs.keymapId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        FragmentConfigKeymapBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel

            viewPager.adapter = ConfigKeymapPagerAdapter(this@ConfigKeymapFragment)

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = strArray(R.array.config_keymap_tab_titles)[position]
            }.attach()

            requireActivity().onBackPressedDispatcher.addCallback {
                findNavController().navigateUp()
            }

            appBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        mViewModel.saveKeymap()
                        findNavController().navigateUp()

                        true
                    }

                    R.id.action_help -> {
                        val direction = ConfigKeymapFragmentDirections.actionGlobalHelpFragment()
                        findNavController().navigate(direction)

                        true
                    }

                    else -> false
                }
            }

            mViewModel.startRecordingTriggerInService.observe(viewLifecycleOwner, EventObserver {
                val serviceEnabled = AccessibilityUtils.isServiceEnabled(requireContext())

                if (serviceEnabled) {
                    requireActivity().sendBroadcast(Intent(MyAccessibilityService.ACTION_RECORD_TRIGGER))
                } else {
                    coordinatorLayout.snack(R.string.error_accessibility_service_disabled_record_trigger) {
                        setAction(str(R.string.snackbar_fix)) {
                            AccessibilityUtils.enableService(requireContext())
                        }
                    }
                }
            })

            return this.root
        }
    }
}