package common.filter;

import java.util.List;

import common.rmi.packet.SearchKey;

public interface SeedFilter {

	void filter(List<SearchKey> seeds);
}
