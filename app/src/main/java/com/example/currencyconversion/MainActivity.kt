package com.example.currencyconversion

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.example.currencyconversion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.edtAmountConverted.isEnabled = false

        adapterSpinnerListItems()
        observeInputs()
        swapData()
        clearFields()

        updateRateIndicative()
    }

    private fun adapterSpinnerListItems() {
        val currencies = listOf(
            CurrencyItem(R.drawable.ic_brazil_flag, R.string.label_brazil),
            CurrencyItem(R.drawable.ic_united_states_flag, R.string.label_united_state),
            CurrencyItem(R.drawable.ic_indian_flag, R.string.label_indian),
            CurrencyItem(R.drawable.ic_canadian_flag, R.string.label_canadian),
            CurrencyItem(R.drawable.ic_australian_flag, R.string.label_australian),
            CurrencyItem(R.drawable.ic_japan_flag, R.string.label_japan),
            CurrencyItem(R.drawable.ic_united_kingdom_flag, R.string.label_united_kingdom),
            CurrencyItem(R.drawable.ic_europe_flag, R.string.label_european_union),
            CurrencyItem(R.drawable.ic_argentine_flag, R.string.label_argentine),
            CurrencyItem(R.drawable.ic_uruguay_flag, R.string.label_uruguay)
        )

        val adapter = CurrencyAdapter(this, currencies)
        binding.spAmount.adapter = adapter
        binding.spAmountConverted.adapter = adapter

        updateRateIndicative()
    }

    private fun swapData() {
        binding.btnSwap.setOnClickListener {
            val spAmount = binding.spAmount.selectedItemPosition
            val spAmountConverted = binding.spAmountConverted.selectedItemPosition
            val edtAmount = binding.edtAmount.text.toString()
            val edtAmountConverted = binding.edtAmountConverted.text.toString()

            binding.edtAmount.setText(edtAmountConverted)
            binding.edtAmountConverted.setText(edtAmount)
            binding.spAmount.setSelection(spAmountConverted)
            binding.spAmountConverted.setSelection(spAmount)

            convertAndShow(binding.edtAmount.text?.toString().orEmpty())

            updateRateIndicative()

            binding.btnSwap.animate().rotationBy(180f).setDuration(250).start()
        }
    }

    private fun observeInputs() {
        binding.edtAmount.doOnTextChanged { text, _, _, _ ->
            if (isUpdating) return@doOnTextChanged
            convertAndShow(text?.toString().orEmpty())
        }

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertAndShow(binding.edtAmount.text?.toString().orEmpty())
                updateRateIndicative()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spAmount.onItemSelectedListener = spinnerListener
        binding.spAmountConverted.onItemSelectedListener = spinnerListener

    }

    private fun convertAndShow(raw: String) {
        val from = getSelectedCode(binding.spAmount)
        val to   = getSelectedCode(binding.spAmountConverted)

        if (raw.isBlank()) {
            isUpdating = true
            binding.edtAmountConverted.setText("")
            isUpdating = false
            return
        }

        val amount = raw.replace(',', '.').toDoubleOrNull() ?: 0.0
        val result = ExchangeRates.convert(amount, from, to)

        if (!result.isNaN()) {
            val decimals = when (to) {
                "JPY" -> 0
                else  -> 2
            }
            val formatted = "%.${decimals}f".format(result)
            if (binding.edtAmountConverted.text?.toString() != formatted) {
                isUpdating = true
                binding.edtAmountConverted.setText(formatted)
                isUpdating = false
            }
        } else {
            isUpdating = true
            binding.edtAmountConverted.setText("")
            isUpdating = false
        }
    }

    private fun getSelectedCode(spinner: Spinner): String {
        val ctx = spinner.context
        val item = spinner.selectedItem
        return when (item) {
            is CurrencyItem -> ctx.getString(item.code)
            is Map<*, *>    -> {
                val v = item["code"]
                if (v is Int) ctx.getString(v) else v.toString()
            }
            is Int          -> ctx.getString(item)
            is String       -> item
            else            -> item.toString()
        }
    }

    private fun rateIndicative(from: CurrencyItem, to: CurrencyItem): String {
        val ctx = binding.root.context
        val fromCode = ctx.getString(from.code)
        val toCode   = ctx.getString(to.code)

        val oneUnit = ExchangeRates.convert(1.0, fromCode, toCode)

        return ctx.getString(
            R.string.subtitle_rate_value,
            fromCode,
            oneUnit,
            toCode
        )
    }

    private fun updateRateIndicative() {
        val fromItem = binding.spAmount.selectedItem as? CurrencyItem ?: return
        val toItem   = binding.spAmountConverted.selectedItem as? CurrencyItem ?: return
        binding.tvRateValue.text = rateIndicative(fromItem, toItem)
    }

    private fun clearFields() {
        binding.btnClear.setOnClickListener {
            binding.edtAmount.text = null
            binding.edtAmountConverted.text = null
        }
    }
}