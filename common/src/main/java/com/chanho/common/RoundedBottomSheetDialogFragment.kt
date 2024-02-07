package com.chanho.common

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.chanho.common.databinding.FragmentRoundedBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RoundedBottomSheetDialogFragment(
    private val listener: OnClickListener? = null,
    private val title: CharSequence? = null,
    private val subtitle1: CharSequence? = null,
    private val contentResource: Int? = null,
    private val subtitle2: CharSequence? = null,
    private val subtitle3: CharSequence? = null,
    private val negative: CharSequence? = null,
    private val positive: CharSequence? = null,
    private val buttonOrientation: Int = LinearLayout.HORIZONTAL,
    private val positiveBackground: Int? = null
) : BottomSheetDialogFragment() {

    interface OnClickListener {
        fun onPositiveClick()
        fun onNegativeClick()
        fun onBindContent(view: View)
        fun onCancel()
        fun onDismiss()
    }

    private lateinit var binding: FragmentRoundedBottomSheetDialogBinding

    private val positiveClickListener = View.OnClickListener {
        listener?.onPositiveClick()
        dismiss()
    }

    private val negativeClickListener = View.OnClickListener {
        listener?.onNegativeClick()
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRoundedBottomSheetDialogBinding.inflate(inflater)

        title?.let {
            binding.title.text = it
        } ?: run {
            binding.title.visibility = View.GONE
        }

        subtitle1?.let {
            binding.subTitle1.text = it
        } ?: run {
            binding.subTitle1.visibility = View.GONE
        }

        contentResource?.let {
            binding.content.layoutResource = it
            val viewStubLayout = binding.content.inflate()
            listener?.onBindContent(viewStubLayout)
        } ?: run {
            binding.content.visibility = View.GONE
        }

        subtitle2?.let {
            binding.subTitle2.text = it
        } ?: run {
            binding.subTitle2.visibility = View.GONE
        }

        subtitle3?.let {
            binding.subTitle3.text = it
        } ?: run {
            binding.subTitle3.visibility = View.GONE
        }

        negative?.let {
            binding.negativeButtonHorizontal.text = it
            binding.negativeButtonVertical.text = it
        } ?: run {
            binding.negativeButtonHorizontal.visibility = View.GONE
            binding.negativeButtonVertical.visibility = View.GONE
        }

        positive?.let {
            binding.positiveButtonHorizontal.text = it
            binding.positiveButtonVertical.text = it
        } ?: run {
            binding.positiveButtonHorizontal.visibility = View.GONE
            binding.positiveButtonVertical.visibility = View.GONE
        }

        positiveBackground?.let {
            binding.positiveButtonHorizontal.background = resources.getDrawable(it, null)
        }

        if (buttonOrientation == LinearLayout.HORIZONTAL) {
            binding.buttonLayoutHorizontal.visibility = View.VISIBLE
            binding.buttonLayoutVertical.visibility = View.GONE
        } else {
            binding.buttonLayoutHorizontal.visibility = View.GONE
            binding.buttonLayoutVertical.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.positiveButtonHorizontal.setOnClickListener(positiveClickListener)
        binding.positiveButtonVertical.setOnClickListener(positiveClickListener)

        binding.negativeButtonHorizontal.setOnClickListener(negativeClickListener)
        binding.negativeButtonVertical.setOnClickListener(negativeClickListener)

        // 팝업 생성 시 전체화면으로 띄우기
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        binding.close.setOnClickListener {
            listener?.onDismiss()
            dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        listener?.onCancel()
        super.onCancel(dialog)
    }

}
