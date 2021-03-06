package decoder.mpm;

import core.exception.DuplicateTagException;
import core.exception.InvalidTagException;
import core.exception.PresentedModeException;
import core.model.mpm.TagLengthString;
import core.utils.TLVUtils;
import model.mpm.AdditionalDataField;
import model.mpm.PaymentSystemSpecificTemplate;
import model.mpm.constants.AdditionalDataFieldCodes;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

// @formatter:off
public final class AdditionalDataFieldDecoder extends DecoderMpm<AdditionalDataField> {

  private static final Map<String, Entry<Class<?>, BiConsumer<AdditionalDataField, ?>>> mapConsumers = new HashMap<>();

  static {
    mapConsumers.put(AdditionalDataFieldCodes.ID_BILL_NUMBER, consumerTagLengthValue(String.class, AdditionalDataField::setBillNumber));
    mapConsumers.put(AdditionalDataFieldCodes.ID_MOBILE_NUMBER, consumerTagLengthValue(String.class, AdditionalDataField::setMobileNumber));
    mapConsumers.put(AdditionalDataFieldCodes.ID_STORE_LABEL, consumerTagLengthValue(String.class, AdditionalDataField::setStoreLabel));
    mapConsumers.put(AdditionalDataFieldCodes.ID_LOYALTY_NUMBER, consumerTagLengthValue(String.class, AdditionalDataField::setLoyaltyNumber));
    mapConsumers.put(AdditionalDataFieldCodes.ID_REFERENCE_LABEL, consumerTagLengthValue(String.class, AdditionalDataField::setReferenceLabel));
    mapConsumers.put(AdditionalDataFieldCodes.ID_CUSTOMER_LABEL, consumerTagLengthValue(String.class, AdditionalDataField::setCustomerLabel));
    mapConsumers.put(AdditionalDataFieldCodes.ID_TERMINAL_LABEL, consumerTagLengthValue(String.class, AdditionalDataField::setTerminalLabel));
    mapConsumers.put(AdditionalDataFieldCodes.ID_PURPOSE_TRANSACTION, consumerTagLengthValue(String.class, AdditionalDataField::setPurposeTransaction));
    mapConsumers.put(AdditionalDataFieldCodes.ID_RFU_FOR_EMVCO, consumerTagLengthValue(TagLengthString.class, AdditionalDataField::addRFUforEMVCo));
    mapConsumers.put(AdditionalDataFieldCodes.ID_PAYMENT_SYSTEM_SPECIFIC, consumerTagLengthValue(PaymentSystemSpecificTemplate.class, AdditionalDataField::addPaymentSystemSpecific));
    mapConsumers.put(AdditionalDataFieldCodes.ID_ADDITIONAL_CONSUMER_DATA_REQUEST, consumerTagLengthValue(String.class, AdditionalDataField::setAdditionalConsumerDataRequest));
  }

  public AdditionalDataFieldDecoder(final String source) {
    super(TLVUtils.valueOf(source));
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected AdditionalDataField decode() throws PresentedModeException {

    final Set<String> tags = new HashSet<>();

    final AdditionalDataField result = new AdditionalDataField();

    while(iterator.hasNext()) {
      final String value = iterator.next();

      final String tag = TLVUtils.valueOfTag(value);

      final String derivateId = derivateId(tag);

      if (tags.contains(tag)) {
        throw new DuplicateTagException("AdditionalDataField", tag, value);
      }

      tags.add(tag);

      final Entry<Class<?>, BiConsumer<AdditionalDataField, ?>> entry = mapConsumers.get(derivateId);

      if (Objects.isNull(entry)) {
        throw new InvalidTagException("AdditionalDataField", tag, value);
      }

      final Class<?> clazz = entry.getKey();

      final BiConsumer consumer = entry.getValue();

      consumer.accept(result, DecoderMpm.decode(value, clazz));
    }

    return result;
  }

  private String derivateId(final String id) {

    if (betweenPaymentSystemSpecificRange(id)) {
      return AdditionalDataFieldCodes.ID_PAYMENT_SYSTEM_SPECIFIC;
    }

    if (betweenRFUForEMVCORange(id)) {
      return AdditionalDataFieldCodes.ID_RFU_FOR_EMVCO;
    }

    return id;
  }

  private boolean betweenRFUForEMVCORange(final String value) {
    return value.compareTo(AdditionalDataFieldCodes.ID_RFU_FOR_EMVCO_RANGE_START) >= 0
        && value.compareTo(AdditionalDataFieldCodes.ID_RFU_FOR_EMVCO_RANGE_END) <= 0;
  }

  private boolean betweenPaymentSystemSpecificRange(final String value) {
    return value.compareTo(AdditionalDataFieldCodes.ID_PAYMENT_SYSTEM_SPECIFIC_TEMPLATES_RANGE_START) >= 0
        && value.compareTo(AdditionalDataFieldCodes.ID_PAYMENT_SYSTEM_SPECIFIC_TEMPLATES_RANGE_END) <= 0;
  }

}
// @formatter:on
