package io.github.sds100.keymapper.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import io.github.sds100.keymapper.NavAppDirections
import io.github.sds100.keymapper.R
import io.github.sds100.keymapper.constraint
import io.github.sds100.keymapper.data.viewmodel.ConstraintListViewModel
import io.github.sds100.keymapper.databinding.FragmentConstraintListBinding
import io.github.sds100.keymapper.ui.fragment.keymap.ConfigKeymapFragment
import io.github.sds100.keymapper.util.TintType
import io.github.sds100.keymapper.util.buildModel
import io.github.sds100.keymapper.util.collectWhenLifecycleStarted
import io.github.sds100.keymapper.util.ifIsData
import splitties.toast.toast

/**
 * Created by sds100 on 29/11/20.
 */
abstract class ConstraintListFragment : Fragment() {

    abstract val constraintListViewModel: ConstraintListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentConstraintListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = constraintListViewModel

            setOnAddConstraintClick {
                val direction = NavAppDirections.actionGlobalChooseConstraint(ConfigKeymapFragment.CHOOSE_CONSTRAINT_REQUEST_KEY)
                findNavController().navigate(direction)
            }

            constraintListViewModel.duplicateConstraintsEvent.collectWhenLifecycleStarted(viewLifecycleOwner, {
                toast(R.string.error_duplicate_constraint)
            })

            constraintListViewModel.buildModelsEvent.collectWhenLifecycleStarted(viewLifecycleOwner, { constraintList ->
                val modelList = constraintList.map { it.buildModel(requireContext()) }
                constraintListViewModel.setModels(modelList)
            })

            subscribeConstraintsList()
        }.root

    private fun FragmentConstraintListBinding.subscribeConstraintsList() {
        constraintListViewModel.modelList.observe(viewLifecycleOwner, { constraintList ->
            epoxyRecyclerViewConstraints.withModels {
                constraintList.ifIsData {
                    it.forEach { constraint ->
                        constraint {
                            id(constraint.id)
                            model(constraint)

                            onRemoveClick { _ ->
                                constraintListViewModel.removeConstraint(constraint.id)
                            }

                            val tintType = when {
                                constraint.hasError -> TintType.ERROR
                                constraint.iconTintOnSurface -> TintType.ON_SURFACE
                                else -> TintType.NONE
                            }

                            tintType(tintType)

                            onFixClick { _ ->
                                constraintListViewModel.onModelClick(constraint.id)
                            }
                        }
                    }
                }
            }
        })
    }
}