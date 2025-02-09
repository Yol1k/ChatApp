package com.example.chatapp.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatapp.databinding.FragmentChatsBinding
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R

class FragmentChatsBinding : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textView.text = "Чаты"

        binding.btnOpenChat.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentChatsBinding_to_fragmentChatBinding2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}