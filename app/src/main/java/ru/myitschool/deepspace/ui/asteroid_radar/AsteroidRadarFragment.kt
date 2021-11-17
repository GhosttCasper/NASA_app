package ru.myitschool.deepspace.ui.asteroid_radar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.myitschool.deepspace.R
import ru.myitschool.deepspace.databinding.FragmentAsteroidRadarBinding
import ru.myitschool.deepspace.utils.DimensionsUtil
import ru.myitschool.deepspace.utils.getColorFromAttributes

/*
 * @author Denis Shaikhlbarin
 */
@DelicateCoroutinesApi
@AndroidEntryPoint
class AsteroidRadarFragment : Fragment() {
    private var _binding: FragmentAsteroidRadarBinding? = null
    private val asteroidViewModel: AsteroidRadarViewModel by viewModels<AsteroidRadarViewModelImpl>()

    private lateinit var asteroidController: AsteroidEpoxyController
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAsteroidRadarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppBar()
        uploadData()
        setupRecycler()
        observeData()

    }

    private fun observeData() {
        asteroidViewModel.getAsteroidListViewModel().observe(viewLifecycleOwner, {
            GlobalScope.launch {
                asteroidController.setData(
                    asteroidViewModel.getAsteroidListViewModel().value
                )

                MainScope().launch {
                    activity?.main_loading?.stopLoadingAnimation(false)
                }
            }
        })
    }

    private fun uploadData() {
        if (!this::asteroidController.isInitialized) {
            asteroidController = AsteroidEpoxyController(requireContext())
            activity?.main_loading?.startLoadingAnimation()

            asteroidViewModel.apply {
                getViewModelScope().launch {
                    getAsteroidList()
                }
            }
        }
    }

    private fun setupRecycler() {
        val list = ArrayList<Long>()
        for (i in -8..-1)
            list.add(AsteroidDateEpoxyModel_().id(-1).id())

        binding.asteroidList.let {
            it.layoutManager = StickyHeaderLinearLayoutManager(requireContext())
            it.adapter = asteroidController.adapter
        }
    }

    private fun initAppBar() {
        DimensionsUtil.dpToPx(requireContext(), 5).let {
            DimensionsUtil.setMargins(
                binding.toolBar,
                it,
                DimensionsUtil.getStatusBarHeight(resources) + it,
                it,
                it
            )
        }

        binding.appbar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                // Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }

                // Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    binding.toolBar.setBackgroundColor(
                        getColorFromAttributes(requireContext(), R.attr.mainBackground)
                    )
                } else {
                    binding.toolBar.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), android.R.color.transparent)
                    )
                }
            }
        })
    }

    override fun onPause() {
        activity?.main_loading?.stopLoadingAnimation()
        super.onPause()
    }
}