package decoder.mpm;


import core.exception.DuplicateTagException;
import core.exception.InvalidTagException;
import core.exception.PresentedModeException;
import core.model.mpm.TagLengthString;
import core.utils.TLVUtils;
import model.mpm.MerchantAccountInformationReservedAdditional;
import model.mpm.constants.MerchantAccountInformationFieldCodes;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

// @formatter:off
public final class MerchantAccountInformationReservedAdditionalDecoder extends DecoderMpm<MerchantAccountInformationReservedAdditional> {

  private static final Map<String, Entry<Class<?>, BiConsumer<MerchantAccountInformationReservedAdditional, ?>>> mapConsumers = new HashMap<>();

  static {
    mapConsumers.put(MerchantAccountInformationFieldCodes.ID_GLOBALLY_UNIQUE_IDENTIFIER, consumerTagLengthValue(String.class, MerchantAccountInformationReservedAdditional::setGloballyUniqueIdentifier));
    mapConsumers.put(MerchantAccountInformationFieldCodes.ID_PAYMENT_NETWORK_SPECIFIC, consumerTagLengthValue(TagLengthString.class, MerchantAccountInformationReservedAdditional::addPaymentNetworkSpecific));
  }

  public MerchantAccountInformationReservedAdditionalDecoder(final String source) {
    super(TLVUtils.valueOf(source));
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected MerchantAccountInformationReservedAdditional decode() throws PresentedModeException {

    final Set<String> tags = new HashSet<>();

    final MerchantAccountInformationReservedAdditional result = new MerchantAccountInformationReservedAdditional();

    while(iterator.hasNext()) {
      final String value = iterator.next();

      final String tag = TLVUtils.valueOfTag(value);

      final String derivateId = derivateId(tag);

      if (tags.contains(tag)) {
        throw new DuplicateTagException("MerchantAccountInformation", tag, value);
      }

      tags.add(tag);

      final Entry<Class<?>, BiConsumer<MerchantAccountInformationReservedAdditional, ?>> entry = mapConsumers.get(derivateId);

      if (Objects.isNull(entry)) {
        throw new InvalidTagException("MerchantAccountInformation", tag, value);
      }

      final Class<?> clazz = entry.getKey();

      final BiConsumer consumer = entry.getValue();

      consumer.accept(result, DecoderMpm.decode(value, clazz));
    }

    return result;
  }

  private String derivateId(final String id) {

    if (betweenPaymentNetworkSpecificRange(id)) {
      return MerchantAccountInformationFieldCodes.ID_PAYMENT_NETWORK_SPECIFIC;
    }

    return id;
  }

  private boolean betweenPaymentNetworkSpecificRange(final String value) {
    return value.compareTo(MerchantAccountInformationFieldCodes.ID_PAYMENT_NETWORK_SPECIFIC_START) >= 0
        && value.compareTo(MerchantAccountInformationFieldCodes.ID_PAYMENT_NETWORK_SPECIFIC_END) <= 0;
  }

}
// @formatter:on
