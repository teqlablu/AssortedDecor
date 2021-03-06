package com.grim3212.assorted.decor.common.util;

import java.util.List;
import java.util.stream.Collectors;

import com.grim3212.assorted.decor.AssortedDecor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ConfigurableBlockStates {

	private final NonNullList<BlockState> loadedStates;

	private ConfigurableBlockStates(NonNullList<BlockState> loadedStates) {
		this.loadedStates = loadedStates;
	}

	public NonNullList<BlockState> getLoadedStates() {
		return loadedStates;
	}

	public static class Builder {

		private final NonNullList<ResourceLocation> blocks;
		private final NonNullList<ResourceLocation> tags;

		public Builder() {
			this.blocks = NonNullList.create();
			this.tags = NonNullList.create();
		}

		public void addBlock(ResourceLocation b) {
			this.blocks.add(b);
		}

		public void addTag(ResourceLocation t) {
			this.tags.add(t);
		}

		public Builder processString(List<String> input) {
			for (String in : input) {
				AssortedDecor.LOGGER.debug("Processing input : " + in);
				if (in != null && !in.isEmpty()) {
					if (in.startsWith("tag|")) {
						String s = in.substring(4);
						AssortedDecor.LOGGER.debug("Add tag [" + s + "]");
						this.addTag(new ResourceLocation(s));
					} else if (in.startsWith("block|")) {
						String s = in.substring(6);
						AssortedDecor.LOGGER.debug("Add block [" + s + "]");
						this.addBlock(new ResourceLocation(s));
					} else {
						AssortedDecor.LOGGER.warn("Can't process input as a configurable block [" + in + "]");
					}
				}
			}
			return this;
		}

		public ConfigurableBlockStates build() {
			NonNullList<BlockState> states = NonNullList.create();

			for (ResourceLocation b : this.blocks) {
				Block block = Registry.BLOCK.getOrDefault(b);

				if (block != Blocks.AIR) {
					states.add(block.getDefaultState());
				} else {
					AssortedDecor.LOGGER.warn(b.toString() + " is not a valid block.");
				}
			}

			for (ResourceLocation t : this.tags) {
				ITag<Block> foundTag = BlockTags.getCollection().get(t);
				if (foundTag != null) {
					states.addAll(foundTag.getAllElements().stream().map(Block::getDefaultState).collect(Collectors.toList()));
				} else {
					AssortedDecor.LOGGER.warn(t.toString() + " is not a valid block tag.");
				}
			}

			return new ConfigurableBlockStates(states);
		}
	}

}
