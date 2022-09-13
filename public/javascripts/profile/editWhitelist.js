// Date Picker
$(function() {
    let startEpochVal = $("#WHITELIST_INVITE_START_EPOCH").val();
    let startEpochSeconds = eval(startEpochVal * 1000);
    let startDateTime = new Date(startEpochSeconds);

    $('input[name="editWhitelistStartEpoch"]').daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        startDate: startDateTime,
        drops:'up',
        locale: {
            format: 'MM/DD/YYYY hh:mm A',
            cancelLabel: 'Clear',
        }
    });

    $('input[name="editWhitelistStartEpoch"]').on('apply.daterangepicker', function(ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_START_EPOCH").val(seconds);
    });

    $('input[name="editWhitelistStartEpoch"]').on('cancel.daterangepicker', function(ev, picker) {
        $(this).val('');
    });

    function setInitialTime(){
        let dateStr = $('input[name="editWhitelistStartEpoch"]').val();
        let date = new Date(dateStr);
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_START_EPOCH").val(seconds);
    }

    setInitialTime();

    let endEpochVal = $("#WHITELIST_INVITE_END_EPOCH").val();
    let endEpochSeconds = eval(endEpochVal * 1000);
    let endDateTime = new Date(endEpochSeconds);

    $('input[name="editWhitelistEndEpoch"]').daterangepicker({
        timePicker: true,
        singleDatePicker: true,
        startDate: endDateTime,
        drops:'up',
        locale: {
            format: 'MM/DD/YYYY hh:mm A',
            cancelLabel: 'Clear',
        }
    });

    $('input[name="editWhitelistEndEpoch"]').on('apply.daterangepicker', function(ev, picker) {
        $(this).val(picker.startDate.format('MM/DD/YYYY hh:mm A'));
        let dateStr = picker.startDate.format('MM/DD/YYYY hh:mm A');
        let date = new Date(dateStr);
        const milliseconds = date.getTime();
        const seconds = Math.floor(date.getTime() / 1000);
        $("#WHITELIST_INVITE_END_EPOCH").val(seconds);
    });

    $('input[name="editWhitelistEndEpoch"]').on('cancel.daterangepicker', function(ev, picker) {
        $(this).val('');
    });
});