package my_app.data;

import my_app.screens.PrimitiveListFormScreen.PrimitiveListFormScreenViewModel;

import java.util.List;

public record TableData(
        List<PrimitiveListFormScreenViewModel.PrimitiveData> primitiveDataList
) {
}